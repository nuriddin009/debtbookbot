package com.example.debtbook_backend.bot;


import com.example.debtbook_backend.entity.*;
import com.example.debtbook_backend.projection.CustomDebtor;
import com.example.debtbook_backend.projection.DebtorProjection;
import com.example.debtbook_backend.projection.ExcelDebtor;
import com.example.debtbook_backend.repository.*;
import com.example.debtbook_backend.service.ExcelFileCreator;
import com.example.debtbook_backend.utils.Role;
import com.example.debtbook_backend.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberMember;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Transactional
@RequiredArgsConstructor
@Component
public class DebtBook
        extends TelegramLongPollingBot {

    @Value("${spring.telegram.bot.username}")
    private String username;

    @Value("${spring.telegram.bot.token}")
    private String token;


    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final DebtRepository debtRepository;
    private final DebtorRepository debtorRepository;
    private final PaymentRepository paymentRepository;
    private final ExcelFileCreator excelFileCreator;
    private static final Integer CACHETIME = 0;



    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage()) {
            Message message = update.getMessage();
            User from = message.getFrom();
            String chatId = message.getChatId().toString();
            BotUser user = getUser(chatId);


            if (message.hasText()) {
                String text = message.getText();

                if (text.equals("/start")) {
                    if (user.getChatId().equals("1486914669")) {
                        user.setRole(Role.SUPER_USER);
                        user.setStep(BotSteps.MAIN_MENU);
                        sendMenuToSuperUser(user);
                        saveUserChanges(user);
                    } else {
                        user.setRole(Role.USER);
                        user.setMyQuery("asdasdad");
                        saveUserChanges(user);
                        if (user.getSelected_language() != null) {
                            if (user.getPhoneNumber() != null) {
                                if (user.getFullName() != null) {
                                    if (user.getStore() != null) {
                                        if (user.getStore().getLatitude() != null) {
                                            user.setStep(BotSteps.MAIN_MENU);
                                            user.setMyOffset(0);
                                            sendStoreOwnerMenu(user);
                                            saveUserChanges(user);
                                        } else {
                                            user.setStep(BotSteps.LOCATION);
                                            submitYourStoreLocation(user);
                                            saveUserChanges(user);
                                        }
                                    } else {
                                        user.setStep(BotSteps.ADD_STORE);
                                        sendText(user, Lang.askStoreName(user));
                                        saveUserChanges(user);
                                    }
                                } else {
                                    user.setStep(BotSteps.ENTER_NAME);
                                    enterYourFullNameRequest(user);
                                    saveUserChanges(user);
                                }
                            } else {
                                user.setStep(BotSteps.SEND_CONTACT);
                                sendRequestContactToUser(user);
                                saveUserChanges(user);
                            }
                        } else {
                            gatheringLanguage(user, from);
                        }
                    }

                } else if (user.getStep().equals(BotSteps.CHANGE_DEBTOR)) {
                    deleteMsg(user, user.getContactBtnId());
                    deleteMsg(user, message.getMessageId());
                    if (text.startsWith("/")) {

                        UUID uuid = UUID.fromString(text.substring(1, text.length()).replace("_", "-"));
                        Optional<Debtor> byId = debtorRepository.findById(uuid);
                        if (byId.isPresent()) {
                            Debtor debtor = debtorRepository.findById(uuid).get();
                            sendInlineButtonMsg(
                                    user,
                                    Lang.changingDebtorAsk(user, debtor),
                                    BotUtils.changeDebtorMenuButton(user, debtor)
                            );
                            user.setDebtorId(debtor.getId());
                            saveUserChanges(user);
                        } else {
                            sendText(user, Lang.notFoundUser(user));
                        }
                    } else {
                        sendText(user, Lang.notFoundUser(user));
                    }
                } else if (user.getStep().equals(BotSteps.DEBTOR_NAME)) {
                    if (text.length() < 64) {
                        deleteMsg(user, user.getContactBtnId());
                        deleteMsg(user, message.getMessageId());
                        user.setStep(BotSteps.MAIN_MENU);
                        Debtor debtor = debtorRepository.findById(user.getDebtorId()).get();
                        debtor.setDebtorFullName(text);
                        debtorRepository.save(debtor);
                        sendStoreOwnerMenu(user);
                        saveUserChanges(user);
                    } else {
                        deleteMsg(user, message.getMessageId());
                        sendText(user, Lang.errorDebtorName(user));
                    }
                } else if (user.getStep().equals(BotSteps.DEBTOR_PHONE)) {
                    if ((text.length() == 13) && (text.startsWith("+998"))
                            && (Pattern.matches("[0-9]+", text.substring(1, text.length())))
                    ) {
                        Debtor debtor = debtorRepository.findById(user.getDebtorId()).get();
                        debtor.setPhoneNumber(text);
                        saveDebtorChanges(debtor);
                        deleteMsg(user, user.getContactBtnId());
                        deleteMsg(user, message.getMessageId());
                        user.setStep(BotSteps.MAIN_MENU);
                        sendStoreOwnerMenu(user);
                        saveUserChanges(user);
                    } else {
                        deleteMsg(user, message.getMessageId());
                        sendText(user, Lang.errorReqMsgPhoneNum(user));
                    }
                } else if (user.getStep().equals(BotSteps.OWNER_NAME)) {
                    if (text.length() < 64) {
                        user.setFullName(text);
                        user.setStep(BotSteps.MAIN_MENU);
                        deleteMsg(user, user.getContactBtnId());
                        deleteMsg(user, message.getMessageId());
                        sendStoreOwnerMenu(user);
                        saveUserChanges(user);
                    } else {
                        deleteMsg(user, message.getMessageId());
                        sendText(user, Lang.enterYourRealName(user));
                    }
                } else if (user.getStep().equals(BotSteps.STORE_NAME) && text.length() < 100) {
                    Store store = user.getStore();
                    store.setStoreName(text);
                    Store save = storeRepository.save(store);
                    user.setStore(save);
                    user.setStep(BotSteps.MAIN_MENU);
                    sendStoreOwnerMenu(user);
                    deleteMsg(user, user.getContactBtnId());
                    deleteMsg(user, message.getMessageId());
                    saveUserChanges(user);
                } else if (user.getStep().equals(BotSteps.ENTER_NAME)) {

                    if (text.length() < 100) {
                        user.setFullName(text);
                        deleteMsg(user, user.getLangBntId()); // bu toliq ismni xabarini o'chiradi
                        deleteMsg(user, message.getMessageId()); // kiritgan ismini o'chiradi
                        user.setStep(BotSteps.ADD_STORE);
                        sendText(user, Lang.askStoreName(user));
                        saveUserChanges(user);
                    } else {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(user.getChatId());
                        sendMessage.setText(Lang.enterYourRealName(user));
                        execute(sendMessage);
                    }
                } else if (user.getStep().equals(BotSteps.MAIN_MENU)) {
                    if (user.getRole().equals(Role.SUPER_USER)) {
                        if (text.equals(Variables.SEE_STORES_UZ)
                                || text.equals(Variables.SEE_STORES_RU) || text.equals(Variables.SEE_STORES_ENG)) {
                            user.setStep(BotSteps.ADMIN_STORE);
                            ByteArrayInputStream storesExcelFile = excelFileCreator.createStoresExcelFile(userRepository.getAllStores());
                            SendDocument sendDocument = new SendDocument();
                            sendDocument.setDocument(new InputFile(storesExcelFile, Lang.storesBtnText(user) + ".xlsx"));
                            sendDocument.setChatId(user.getChatId());
                            sendDocument.setReplyMarkup(BotUtils.goToHomeButton(user));
                            Message execute = execute(sendDocument);
                            user.setMsgId(execute.getMessageId());
                            saveUserChanges(user);
                            deleteMsg(user, message.getMessageId());
                            deleteMsg(user, user.getStartMsgId());
                        } else if (text.equals(Variables.CUSTOMERS_UZ)
                                || text.equals(Variables.CUSTOMERS_RU) || text.equals(Variables.CUSTOMERS_ENG)) {
                            user.setStep(BotSteps.ADMIN_DEBTOR);
                            ByteArrayInputStream inputStream = excelFileCreator.createDebtorsExcelFile(debtorRepository.getAllDebtorsList());
                            SendDocument sendDocument = new SendDocument();
                            sendDocument.setDocument(new InputFile(inputStream, Lang.debtorText(user) + ".xlsx"));
                            sendDocument.setChatId(user.getChatId());
                            sendDocument.setReplyMarkup(BotUtils.goToHomeButton(user));
                            Message execute = execute(sendDocument);
                            user.setMsgId(execute.getMessageId());
                            saveUserChanges(user);
                            deleteMsg(user, message.getMessageId());
                            deleteMsg(user, user.getStartMsgId());
                        }
                    } else {

                        if (text.startsWith("/")) {
                            if (text.equals("/newDebtor")) {
                                deleteMsg(user, user.getMenuBntId());
                                deleteMsg(user, message.getMessageId());
                                String myQuery = user.getMyQuery();
                                if ((myQuery.length() == 13) && (myQuery.startsWith("+998"))
                                        && (Pattern.matches("[0-9]+", myQuery.substring(1, myQuery.length())))
                                ) {
                                    user.setStep(BotSteps.ADD_DEBTOR_NAME);
                                    Debtor debtor = new Debtor();
                                    debtor.setPhoneNumber(myQuery);
                                    Debtor save = debtorRepository.save(debtor);
                                    user.setDebtorId(save.getId());
//                                    sendText(user, myQuery + "\n" + Lang.addDebtorNameText(user));
                                    sendInlineButtonMsg(user, myQuery + "\n" + Lang.addDebtorNameText(user), BotUtils.cancelButton(user));
                                    saveUserChanges(user);
                                } else {
                                    user.setStep(BotSteps.ADD_DEBTOR);
                                    sendInlineButtonMsg(user, Lang.askDebtorPhoneFromAdmin(user), BotUtils.cancelButton(user));
                                    saveUserChanges(user);
                                }
                            } else {
                                try {
                                    Optional<Debtor> byId = debtorRepository
                                            .findById(UUID.fromString(text.substring(1, text.length())
                                                    .replace("_", "-")));
                                    if (byId.isPresent()) {
                                        deleteMsg(user, user.getMenuBntId());
                                        deleteMsg(user, message.getMessageId());
                                        Debtor debtor = byId.get();
                                        String debtorText = debtor.getDebtorFullName() + "\n" +
                                                debtor.getPhoneNumber() + "\n" +
                                                debtRepository.getOneDebtorDebt(
                                                        user.getStore().getId(),
                                                        debtor.getId(), 0L, 0L,
                                                        Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user),
                                                        Lang.leftOverText(user)
                                                );
                                        user.setStep(BotSteps.DEBT_MENU);
                                        user.setDebtorId(debtor.getId());
                                        sendInlineButtonMsg(user, debtorText, BotUtils.addDecDebtButtons(user));
                                        saveUserChanges(user);
                                    } else {
                                        sendText(user, Lang.notFoundUser(user));
                                    }
                                } catch (Exception e) {
                                    sendText(user, Lang.notFoundUser(user));
                                }
                            }
                        } else {
                            sendText(user, Lang.notFoundUser(user));
                        }

                    }
                } else if (user.getStep().equals(BotSteps.ADD_STORE)) {
                    if (text.length() < 100) {
                        Store store = new Store();
                        store.setStoreName(text);
                        Store save = storeRepository.save(store);
                        user.setStore(save);
                        user.setStep(BotSteps.LOCATION);
                        submitYourStoreLocation(user);
                        saveUserChanges(user);
                        deleteMsg(user, user.getMsgId());
                        deleteMsg(user, message.getMessageId());
                    } else {
                        sendText(user, Lang.sorryStoreNameDoesNotExist(user));
                    }
                } else if (user.getStep().equals(BotSteps.ADD_DEBTOR)) {
                    if ((text.length() == 13) && (text.startsWith("+998"))
                            && (Pattern.matches("[0-9]+", text.substring(1, text.length())))
                    ) {
                        Debtor debtor = new Debtor();

                        UUID storeId = user.getStore().getId();
                        if (debtorRepository.getIsSavedDebtor(storeId, text).isPresent()) {
                            CustomDebtor debtor1 = debtorRepository.getIsSavedDebtor(storeId, text).get();
                            user.setDebtorId(debtor1.getId());
                            user.setStep(BotSteps.DEBT_MENU);
                            UUID debtorId = debtor1.getId();
                            String debtText = debtor1.getFullName() + "\n" + debtor1.getPhoneNumber() + "\n" +
                                    debtRepository.getOneDebtorDebt(storeId, debtorId, 0L, 0L,
                                            Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user));

                            deleteMsg(user, user.getContactBtnId());
                            deleteMsg(user, message.getMessageId());
                            sendInlineButtonMsg(
                                    user,
                                    debtText,
                                    BotUtils.addDecDebtButtons(user)
                            );
                            saveUserChanges(user);
                        } else {
                            deleteMsg(user, user.getContactBtnId());
                            debtor.setPhoneNumber(text);
                            Debtor save = debtorRepository.save(debtor);
                            user.setDebtorId(save.getId());
                            user.setStep(BotSteps.ADD_DEBTOR_NAME);
//                            sendText(user, save.getPhoneNumber() + "\n" + Lang.addDebtorNameText(user));
                            sendInlineButtonMsg(
                                    user,
                                    save.getPhoneNumber() + "\n" + Lang.addDebtorNameText(user),
                                    BotUtils.cancelButton(user)
                            );
                            deleteMsg(user, message.getMessageId());
                            saveUserChanges(user);
                        }
                    } else {
                        deleteMsg(user, message.getMessageId());
                        sendText(user, Lang.errorReqMsgPhoneNum(user));
                    }
                } else if (user.getStep().equals(BotSteps.ADD_DEBTOR_NAME)) {

                    if (text.length() < 64) {
                        Debtor debtor = debtorRepository.findById(user.getDebtorId()).get();
                        debtor.setDebtorFullName(text);
                        user.setStep(BotSteps.ADD_DEBT);
                        user.setMyQuery("asdasdasd");
                        saveDebtorChanges(debtor);

                        String debtText = debtor.getDebtorFullName() + "\n" + debtor.getPhoneNumber() + "\n" +
                                debtRepository.getOneDebtorDebt(user.getStore().getId(), debtor.getId(), 0L, 0L, Lang.paidDebtRepoTxt(user),
                                        Lang.debtRepoTxt(user), Lang.leftOverText(user)) + "\n" +
                                Lang.howMuchLoanGetFirstTime(user);
//                        deleteMsg(user, user.getMsgId());
                        deleteMsg(user, user.getContactBtnId());
                        sendText(user, debtText);
                        deleteMsg(user, message.getMessageId());
                        saveUserChanges(user);
                    } else {
                        sendText(user, Lang.errorDebtorName(user));
                    }
                } else if (user.getStep().equals(BotSteps.ADD_DEBT)) {
                    Debtor debtor = debtorRepository.findById(user.getDebtorId()).get();
                    Store store = user.getStore();

                    if (Pattern.matches("[0-9]+", text)) {

                        if (text.length() <= 15) {
                            if (Long.parseLong(text) < Long.MAX_VALUE) {
                                deleteMsg(user, user.getMsgId());
//                                deleteMsg(user,user.getContactBtnId());
                                deleteMsg(user, message.getMessageId());


                                String confirmText = debtor.getDebtorFullName() + "\n" + debtor.getPhoneNumber() + "\n" +
                                        Lang.ediText(user) + " " + debtRepository.getOneDebtorDebt(user.getStore().getId(), debtor.getId(),
                                        0L, 0L, Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user)) + "\n" +
                                        Lang.happenText(user) + " " + debtRepository.getOneDebtorDebt(user.getStore().getId(), debtor.getId(),
                                        Long.parseLong(text), 0L, Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user));

                                sendInlineButtonMsg(user, confirmText, BotUtils.confirmOrCancelButtons(user));
                                Store store1 = user.getStore();
                                store1.setConfirmationDebt(Long.parseLong(text));
                                storeRepository.save(store);
                                user.setStep(BotSteps.ADD_DEBT_CONFIRMATION);
                                user.setMyOffset(0);
                                saveUserChanges(user);
                            }
                        } else {
                            sendText(user, Lang.numberVeryBig(user));
                        }


                    } else {
                        sendText(user, Lang.enterTrueNumber(user));
                    }
                } else if (user.getStep().equals(BotSteps.REDUCE_DEBT)) {
                    Debtor debtor = debtorRepository.findById(user.getDebtorId()).get();

                    if (Pattern.matches("[0-9]+", text)) {


                        if (paymentRepository.debtPayments(debtor.getId()) + Long.parseLong(text) <=
                                debtRepository.getOneDebtorsDebt(user.getStore().getId(), debtor.getId())) {
                            Store store = user.getStore();
                            store.setConfirmationDebt(Long.parseLong(text));
                            storeRepository.save(store);
                            user.setStep(BotSteps.REDUCE_DEBT_CONFIRMATION);

                            String confirmText = debtor.getDebtorFullName() + "\n"
                                    + debtor.getPhoneNumber() + "\n" +
                                    Lang.ediText(user) + " "
                                    + debtRepository.getOneDebtorDebt(user.getStore().getId(), debtor.getId(),
                                    0L, 0L, Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user)) + "\n" +
                                    Lang.happenText(user) + " "
                                    + debtRepository.getOneDebtorDebt(user.getStore().getId(),
                                    debtor.getId(), 0L, Long.parseLong(text), Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user));

                            sendInlineButtonMsg(user, confirmText, BotUtils.confirmOrCancelButtons(user));
                            deleteMsg(user, user.getMsgId());
                            deleteMsg(user, message.getMessageId());
                            user.setMyOffset(0);
                            saveUserChanges(user);
                        } else {
                            deleteMsg(user, message.getMessageId());
                            sendText(user, Lang.loanBigThanDebt(user));
                        }


                    } else {
                        deleteMsg(user, message.getMessageId());
                        sendText(user, Lang.enterTrueNumber(user));
                    }
                } else if (user.getStep().equals(BotSteps.LIST_DEBTORS)) {

                    if (text.startsWith("/")) {
                        if (text.equals("/newDebtor")) {
                            deleteMsg(user, user.getContactBtnId());
                            deleteMsg(user, message.getMessageId());

                            String myQuery = user.getMyQuery();
                            if ((myQuery.length() == 13) && (myQuery.startsWith("+998"))
                                    && (Pattern.matches("[0-9]+", myQuery.substring(1, myQuery.length())))
                            ) {
                                user.setStep(BotSteps.ADD_DEBTOR_NAME);
                                Debtor debtor = new Debtor();
                                debtor.setPhoneNumber(myQuery);
                                Debtor save = debtorRepository.save(debtor);
                                user.setDebtorId(save.getId());
//                                sendText(user, myQuery + "\n" + Lang.addDebtorNameText(user));
                                sendInlineButtonMsg(user,
                                        myQuery + "\n" + Lang.addDebtorNameText(user),
                                        BotUtils.cancelButton(user));
                                saveUserChanges(user);
                            } else {
                                user.setStep(BotSteps.ADD_DEBTOR);
                                sendInlineButtonMsg(user, Lang.askDebtorPhoneFromAdmin(user), BotUtils.cancelButton(user));
                                saveUserChanges(user);
                            }
                        } else {
                            try {
                                Optional<Debtor> byId = debtorRepository
                                        .findById(UUID.fromString(text.substring(1, text.length())
                                                .replace("_", "-")));
                                if (byId.isPresent()) {
                                    deleteMsg(user, user.getContactBtnId());
                                    deleteMsg(user, message.getMessageId());
                                    Debtor debtor = byId.get();
                                    String debtorText = debtor.getDebtorFullName() + "\n" +
                                            debtor.getPhoneNumber() + "\n" +
                                            debtRepository.getOneDebtorDebt(
                                                    user.getStore().getId(),
                                                    debtor.getId(), 0L, 0L,
                                                    Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user));
                                    user.setStep(BotSteps.DEBT_MENU);
                                    user.setDebtorId(debtor.getId());
                                    sendInlineButtonMsg(user, debtorText, BotUtils.addDecDebtButtons(user));
                                    saveUserChanges(user);
                                } else {
                                    sendText(user, Lang.notFoundUser(user));
                                }
                            } catch (Exception e) {
                                sendText(user, Lang.notFoundUser(user));
                            }
                        }
                    } else {
                        sendText(user, Lang.notFoundUser(user));
                    }
                }
            } else if (message.hasContact()) {
                Contact contact = message.getContact();
                if (user.getStep().equals(BotSteps.SEND_CONTACT)) {
                    user.setPhoneNumber(contact.getPhoneNumber().startsWith("+") ?
                            contact.getPhoneNumber()
                            : "+" + contact.getPhoneNumber());
                    user.setStep(BotSteps.ENTER_NAME);
                    enterYourFullNameRequest(user);
                    saveUserChanges(user);
                    deleteMsg(user, user.getContactBtnId());
                    deleteMsg(user, message.getMessageId());
                } else if (user.getStep().equals(BotSteps.OWNER_PHONE)) {
                    user.setPhoneNumber(contact.getPhoneNumber().startsWith("+") ?
                            contact.getPhoneNumber()
                            : "+" + contact.getPhoneNumber());
                    user.setStep(BotSteps.MAIN_MENU);
                    user.setMyQuery("asdasdasdasd");
                    deleteMsg(user, user.getStartMsgId());
                    deleteMsg(user, message.getMessageId());
                    sendStoreOwnerMenu(user);
                    saveUserChanges(user);
                }
            } else if (message.hasLocation()) {
                Location location = message.getLocation();
                Store store = user.getStore();
                if (user.getStep().equals(BotSteps.LOCATION)) {
                    store.setLatitude(location.getLatitude());
                    store.setLongitude(location.getLongitude());
                    Store save = storeRepository.save(store);
                    user.setStore(save);

                    deleteMsg(user, user.getLocationBtnId());
                    deleteMsg(user, message.getMessageId());

                    user.setStep(BotSteps.START_WORKING);
                    sendInlineButtonMsg(user, Lang.successfullyRegisteredMsg(user),
                            BotUtils.generateSuccessfullyRegisteredButton(user));
                    saveUserChanges(user);
                } else if (user.getStep().equals(BotSteps.STORE_LOCATION)) {
                    store.setLatitude(location.getLatitude());
                    store.setLongitude(location.getLongitude());
                    Store save = storeRepository.save(store);
                    user.setStore(save);
                    deleteMsg(user, user.getLocationBtnId());
                    deleteMsg(user, message.getMessageId());
                    user.setStep(BotSteps.MAIN_MENU);
                    user.setMyQuery("asdasdasd");
                    sendStoreOwnerMenu(user);
                    saveUserChanges(user);
                }
            }


        } else if (update.hasCallbackQuery()) {

            String callBackId = update.getCallbackQuery().getId();
            Message message = update.getCallbackQuery().getMessage();
            String data = update.getCallbackQuery().getData();
            String chatId = message.getChatId().toString();
            BotUser user = getUser(chatId);

            if (user.getStep().equals(BotSteps.SETTINGS_MENU)) {
                if (data.equals(BotCallBackData.CHANGE_LANG)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.CHANGE_LANG);

                    sendInlineButtonMsg(user,
                            Lang.changeLangTxt(user), BotUtils.generateGatheringLanguageButtons());

                    saveUserChanges(user);
                } else if (data.equals(BotCallBackData.GO_HOME)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.MAIN_MENU);
                    sendStoreOwnerMenu(user);
                    saveUserChanges(user);
                } else if (data.equals(BotCallBackData.CHANGE_STORE)) {
                    user.setStep(BotSteps.CHANGE_STORE);
                    deleteMsg(user, user.getContactBtnId());
                    sendInlineButtonMsg(user,
                            Lang.changeStoreInformation(user),
                            BotUtils.changeStoreInformationButtons(user)
                    );
                    saveUserChanges(user);
                } else if (data.equals(BotCallBackData.CHANGE_DEBTOR)) {
                    user.setStep(BotSteps.CHANGE_DEBTOR);
                    deleteMsg(user, user.getContactBtnId());
                    saveUserChanges(user);

                    String debtors = Lang.debtorText(user) + " :\n\n";

                    Page<DebtorProjection> storeDebtors = storeRepository.getStoreDebtors(user.getStore().getId(), PageRequest.of(user.getMyOffset(), 5),
                            Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user));
                    for (int i = 0; i < storeDebtors.getContent().size(); i++) {
                        debtors += i + 1 + " ) " + Lang.nameText(user) + " : " + storeDebtors.getContent().get(i).getFullName() + "\n" +
                                storeDebtors.getContent().get(i).getPhoneNumber() + "\n" + storeDebtors.getContent().get(i).getDebt() + "\n \uD83D\uDD11 :  /" + storeDebtors.getContent().get(i).getId().replace("-", "_") + "\n\n" + "******\n";
                    }

                    List<DebtorProjection> storeAllDebtors = storeRepository.getStoreAllDebtors(user.getStore().getId());

                    if (storeAllDebtors.size() > 5) {
                        sendInlineButtonMsg(user, debtors + Lang.whichDYLChangeDebtor(user), BotUtils.generateSettingsPaginationNextButton(user));
                    } else {
                        sendInlineButtonMsg(user, debtors + Lang.whichDYLChangeDebtor(user), BotUtils.backBtn(user));
                    }


                } else if (data.equals(BotCallBackData.CABINET)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.CABINET);
                    sendInlineButtonMsg(
                            user,
                            Lang.cabinetMenuTxt(user),
                            BotUtils.ownerMenuButtons(user)
                    );
                    saveUserChanges(user);
                }
            } else if (user.getStep().equals(BotSteps.ADD_DEBTOR_NAME)) {
                if (data.equals(BotCallBackData.GO_HOME)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.MAIN_MENU);
                    user.setMyQuery("asdasdasd");
                    sendStoreOwnerMenu(user);
                    saveUserChanges(user);
                }
            } else if (user.getStep().equals(BotSteps.CABINET)) {
                if (data.equals(BotCallBackData.CANCEL)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.SETTINGS_MENU);
                    sendInlineButtonMsg(
                            user,
                            Lang.settingsMenuTitleTxt(user),
                            BotUtils.settingsMenuButtons(user, storeRepository.getStoreAllDebtors(user.getStore().getId()).size())
                    );
                    saveUserChanges(user);
                } else if (data.equals(BotCallBackData.OWNER_NAME)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.OWNER_NAME);
                    sendInlineButtonMsg(
                            user,
                            Lang.nameText(user) + ": " + user.getFullName() + "\n" +
                                    Lang.enterYourFullNameRequestText(user),
                            BotUtils.backBtn(user)
                    );
                    saveUserChanges(user);
                } else if (data.equals(BotCallBackData.OWNER_PHONE)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.OWNER_PHONE);
                    changeOwnerContactRequest(user);
                    saveUserChanges(user);
                }
            } else if (user.getStep().equals(BotSteps.OWNER_NAME)) {
                if (data.equals(BotCallBackData.GO_HOME)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.CABINET);
                    sendInlineButtonMsg(
                            user,
                            Lang.cabinetMenuTxt(user),
                            BotUtils.ownerMenuButtons(user)
                    );
                    saveUserChanges(user);
                }
            } else if (user.getStep().equals(BotSteps.CHANGE_DEBTOR)) {
                String debtors = Lang.debtorText(user) + " :\n\n";
                List<DebtorProjection> storeDebtors = storeRepository.getStoreAllDebtors(user.getStore().getId());
                for (int i = 0; i < storeDebtors.size(); i++) {
                    debtors += i + 1 + " ) " + Lang.nameText(user) + " : " + storeDebtors.get(i).getFullName() + "\n" +
                            storeDebtors.get(i).getPhoneNumber() + "\n\n" + "********************\n";
                }
                if (data.equals(BotCallBackData.GO_HOME)) {
                    user.setStep(BotSteps.MAIN_MENU);
                    user.setMyOffset(0);
                    deleteMsg(user, user.getContactBtnId());
                    sendStoreOwnerMenu(user);
                    saveUserChanges(user);
                } else if (data.equals(BotCallBackData.PREVIOUS)) {
                    user.setMyOffset(user.getMyOffset() - 1);
                    saveUserChanges(user);
                    String debtorsText = Lang.debtorText(user) + " :\n\n";
                    PageRequest pageRequest = PageRequest.of(user.getMyOffset(), 5);
                    Page<DebtorProjection> debtorsList = storeRepository.getStoreDebtors(user.getStore().getId(), pageRequest,
                            Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user));
                    for (int i = 0; i < debtorsList.getContent().size(); i++) {
                        debtorsText += i + 1 + " ) " + Lang.nameText(user) + " : " + debtorsList.getContent().get(i).getFullName() + "\n" +
                                debtorsList.getContent().get(i).getPhoneNumber() + "\n" + debtorsList.getContent().get(i).getDebt() + "\n \uD83D\uDD11 :  /" + debtorsList.getContent().get(i).getId().replace("-", "_") + "\n\n" + "******\n";
                    }
                    int i = storeDebtors.size() - debtorsList.getTotalPages() * 5;
                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setChatId(user.getChatId());
                    editMessageText.setText(debtorsText);
                    editMessageText.setMessageId(user.getContactBtnId());
                    if (user.getMyOffset() == 0) {
                        editMessageText.setReplyMarkup(BotUtils.generateSettingsPaginationNextButton(user));
                    } else if (user.getMyOffset() < debtorsList.getTotalPages() && user.getMyOffset() > 0) {
                        editMessageText.setReplyMarkup(BotUtils.generateSettingsPaginationButton(user));
                    }
                    execute(editMessageText);
                } else if (data.equals(BotCallBackData.NEXT)) {
                    user.setMyOffset(user.getMyOffset() + 1);
                    saveUserChanges(user);
                    String debtorsText = Lang.debtorText(user) + " :\n\n";

                    PageRequest pageRequest = PageRequest.of(user.getMyOffset(), 5);
                    Page<DebtorProjection> debtorsList = storeRepository.getStoreDebtors(user.getStore().getId(), pageRequest,
                            Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user));


                    for (int i = 0; i < debtorsList.getContent().size(); i++) {
                        debtorsText += i + 1 + " ) " + Lang.nameText(user) + " : " + debtorsList.getContent().get(i).getFullName() + "\n" +
                                debtorsList.getContent().get(i).getPhoneNumber() + "\n" + debtorsList.getContent().get(i).getDebt() + "\n \uD83D\uDD11 :  /" + debtorsList.getContent().get(i).getId().replace("-", "_") + "\n\n" + "******\n";
                    }


                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setChatId(user.getChatId());
                    editMessageText.setText(debtorsText);
                    editMessageText.setMessageId(user.getContactBtnId());
                    if (user.getMyOffset() + 2 <= debtorsList.getTotalPages() && user.getMyOffset() > 0) {
                        editMessageText.setReplyMarkup(BotUtils.generateSettingsPaginationButton(user));
                    } else if (debtorsList.getTotalPages() == user.getMyOffset() + 1) {
                        editMessageText.setReplyMarkup(BotUtils.generateSettingsPaginationPrevButton(user));
                    }
                    execute(editMessageText);
                } else if (data.equals(BotCallBackData.CANCEL)) {
                    deleteMsg(user, user.getContactBtnId());

                    user.setMyOffset(0);
                    saveUserChanges(user);
                    String debtorsText = Lang.debtorText(user) + " :\n\n";

                    PageRequest pageRequest = PageRequest.of(user.getMyOffset(), 5);
                    Page<DebtorProjection> debtorsList = storeRepository.getStoreDebtors(user.getStore().getId(), pageRequest,
                            Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user));


                    for (int i = 0; i < debtorsList.getContent().size(); i++) {
                        debtorsText += i + 1 + " ) " + Lang.nameText(user) + " : " + debtorsList.getContent().get(i).getFullName() + "\n" +
                                debtorsList.getContent().get(i).getPhoneNumber() + "\n" + debtorsList.getContent().get(i).getDebt() + "\n \uD83D\uDD11 :  /" + debtorsList.getContent().get(i).getId().replace("-", "_") + "\n\n" + "******\n";
                    }

                    if (storeDebtors.size() > 5) {
                        sendInlineButtonMsg(user, debtorsText, BotUtils.generateSettingsPaginationNextButton(user));
                    } else {
                        sendInlineButtonMsg(user, debtorsText, BotUtils.backBtn(user));
                    }

                } else if (data.equals(BotCallBackData.DEBTOR_NAME)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.DEBTOR_NAME);
                    Debtor debtor = debtorRepository.findById(user.getDebtorId()).get();
                    sendInlineButtonMsg(
                            user,
                            Lang.debtorNameTxt(user, debtor) + Lang.changeDebtorNameTitle(user),
                            BotUtils.backBtn(user)
                    );
                    saveUserChanges(user);
                } else if (data.equals(BotCallBackData.DEBTOR_PHONE)) {
                    user.setStep(BotSteps.DEBTOR_PHONE);
                    deleteMsg(user, user.getContactBtnId());
                    Debtor debtor = debtorRepository.findById(user.getDebtorId()).get();
                    sendInlineButtonMsg(
                            user,
                            Lang.changingDebtorAsking(user, debtor) + "\n" + Lang.askDebtorPhoneFromAdmin(user),
                            BotUtils.backBtn(user)
                    );
                    saveUserChanges(user);
                }
            } else if (user.getStep().equals(BotSteps.DEBTOR_NAME)) {
                if (data.equals(BotCallBackData.GO_HOME)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.CHANGE_DEBTOR);
                    Debtor debtor = debtorRepository.findById(user.getDebtorId()).get();
                    sendInlineButtonMsg(user,
                            Lang.changingDebtorAsk(user, debtor),
                            BotUtils.changeDebtorMenuButton(user, debtor));
                    saveUserChanges(user);
                }
            } else if (user.getStep().equals(BotSteps.DEBTOR_PHONE)) {
                if (data.equals(BotCallBackData.GO_HOME)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.CHANGE_DEBTOR);
                    Debtor debtor = debtorRepository.findById(user.getDebtorId()).get();
                    sendInlineButtonMsg(user,
                            Lang.changingDebtorAsk(user, debtor),
                            BotUtils.changeDebtorMenuButton(user, debtor));
                    saveUserChanges(user);
                }
            } else if (user.getStep().equals(BotSteps.CHANGE_STORE)) {
                if (data.equals(BotCallBackData.CANCEL)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.SETTINGS_MENU);
                    sendInlineButtonMsg(user,
                            Lang.settingsMenuTitleTxt(user),
                            BotUtils.settingsMenuButtons(user, storeRepository.getStoreAllDebtors(user.getStore().getId()).size()));
                    saveUserChanges(user);
                } else if (data.equals(BotCallBackData.STORE_NAME)) {
                    deleteMsg(user, user.getContactBtnId());
                    sendInlineButtonMsg(user, Lang.newStoreName(user, user.getStore().getStoreName()), BotUtils.backBtn(user));
                    user.setStep(BotSteps.STORE_NAME);
                    saveUserChanges(user);
                } else if (data.equals(BotCallBackData.STORE_LOCATION)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.STORE_LOCATION);
                    submitYourStoreLocation(user);
                    saveUserChanges(user);
                }

            } else if (user.getStep().equals(BotSteps.STORE_NAME)) {
                if (data.equals(BotCallBackData.GO_HOME)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.CHANGE_STORE);
                    sendInlineButtonMsg(user,
                            Lang.changeStoreInformation(user),
                            BotUtils.changeStoreInformationButtons(user));
                }
            } else if (user.getStep().equals(BotSteps.CHANGE_LANG)) {
                if (data.equals("uz")) {
                    user.setSelected_language("uz");
                    user.setStep(BotSteps.MAIN_MENU);
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                    answerCallbackQuery.setText("O'zbek tili tanlandi ✅");
                    answerCallbackQuery.setCallbackQueryId(callBackId);
                    answerCallbackQuery.setCacheTime(1000);
                    answerCallbackQuery.setShowAlert(false);
                    execute(answerCallbackQuery);
                    saveUserChanges(user);
                    deleteMsg(user, user.getContactBtnId());
                    sendStoreOwnerMenu(user);
                } else if (data.equals("ru")) {
                    user.setSelected_language("ru");
                    user.setStep(BotSteps.MAIN_MENU);
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                    answerCallbackQuery.setText("был выбран русский язык ✅");
                    answerCallbackQuery.setCallbackQueryId(callBackId);
                    answerCallbackQuery.setCacheTime(1000);
                    answerCallbackQuery.setShowAlert(false);
                    execute(answerCallbackQuery);
                    saveUserChanges(user);
                    deleteMsg(user, user.getContactBtnId());
                    sendStoreOwnerMenu(user);
                } else {
                    user.setSelected_language("eng");
                    user.setStep(BotSteps.MAIN_MENU);
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                    answerCallbackQuery.setText("English was selected ✅");
                    answerCallbackQuery.setCallbackQueryId(callBackId);
                    answerCallbackQuery.setCacheTime(1000);
                    answerCallbackQuery.setShowAlert(false);
                    execute(answerCallbackQuery);
                    saveUserChanges(user);
                    deleteMsg(user, user.getContactBtnId());
                    sendStoreOwnerMenu(user);
                }
            } else if (user.getStep().equals(BotSteps.SELECT_LANG)) {
                if (data.equals("uz")) {
                    user.setSelected_language("uz");
                } else if (data.equals("ru")) {
                    user.setSelected_language("ru");
                } else {
                    user.setSelected_language("eng");
                }
                sendRequestContactToUser(user);
                user.setStep(BotSteps.SEND_CONTACT);
                saveUserChanges(user);
                deleteMsg(user, user.getLangBntId());
            } else if (user.getStep().equals(BotSteps.START_WORKING)) {
                if (data.equals(BotSteps.START_WORKING)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.MAIN_MENU);
                    sendStoreOwnerMenu(user);
                    saveUserChanges(user);
                } else {

                }

            } else if (user.getStep().equals(BotSteps.ADMIN_DEBTOR) || user.getStep().equals(BotSteps.ADMIN_STORE)) {
                if (user.getRole().equals(Role.SUPER_USER)) {
                    if (data.equals(BotCallBackData.GO_HOME)) {
                       user.setStep(BotSteps.MAIN_MENU);
                        deleteMsg(user, user.getMsgId());
                        user.setMyOffset(0);
                        sendMenuToSuperUser(user);
                        saveUserChanges(user);
                    }
                }
            } else if (user.getStep().equals(BotSteps.MAIN_MENU)) {
                if (user.getRole().equals(Role.SUPER_USER)) {
                    if (data.equals(BotCallBackData.GO_HOME)) {
                        deleteMsg(user, user.getMsgId());
                        user.setMyOffset(0);
                        sendMenuToSuperUser(user);
                        saveUserChanges(user);
                    }
                } else {
                    if (data.equals(BotCallBackData.GET_REPORT)) {
                        deleteMsg(user, user.getMenuBntId());
                        reportInformationByTimes(user);
                    } else if (data.equals(BotCallBackData.ADD_DEBTOR)) {
                        deleteMsg(user, user.getMenuBntId());
                        user.setStep(BotSteps.ADD_DEBTOR);
                        sendInlineButtonMsg(user, Lang.askDebtorPhoneFromAdmin(user), BotUtils.cancelButton(user));
                        saveUserChanges(user);
                    } else if (data.equals(BotCallBackData.GO_HOME)) {
                        deleteMsg(user, user.getStartMsgId());
                        user.setStep(BotSteps.MAIN_MENU);
                        user.setMyOffset(0);
                        sendStoreOwnerMenu(user);
                        saveUserChanges(user);
                    } else if (data.equals(BotCallBackData.LIST_DEBTORS)) {
                        user.setStep(BotSteps.LIST_DEBTORS);
                        deleteMsg(user, user.getMenuBntId());

                        String debtors = Lang.debtorText(user) + " :\n\n";

                        Page<DebtorProjection> storeDebtors = storeRepository.getStoreDebtors(user.getStore().getId(), PageRequest.of(user.getMyOffset(), 5),
                                Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user));
                        for (int i = 0; i < storeDebtors.getContent().size(); i++) {
                            debtors += i + 1 + " ) " + Lang.nameText(user) + " : " + storeDebtors.getContent().get(i).getFullName() + "\n" +
                                    storeDebtors.getContent().get(i).getPhoneNumber() + "\n" + storeDebtors.getContent().get(i).getDebt() + "\n \uD83D\uDD11 :  /" + storeDebtors.getContent().get(i).getId().replace("-", "_") + "\n\n" + "******\n";
                        }

                        List<DebtorProjection> storeAllDebtors = storeRepository.getStoreAllDebtors(user.getStore().getId());

                        if (storeAllDebtors.size() > 5) {
                            sendInlineButtonMsg(user, debtors, BotUtils.generatePaginationNextButton(user));
                        } else {
                            sendInlineButtonMsg(user, debtors, BotUtils.generateExcelButton(user));
                        }

                    } else if (data.equals(BotCallBackData.SETTINGS)) {
                        deleteMsg(user, user.getMenuBntId());
                        user.setStep(BotSteps.SETTINGS_MENU);
                        sendInlineButtonMsg(user, Lang.settingsMenuTitleTxt(user),
                                BotUtils.settingsMenuButtons(user,
                                        storeRepository.getStoreAllDebtors(user.getStore().getId()).size()));
                        saveUserChanges(user);
                    }
                }
            } else if (user.getStep().equals(BotSteps.ADD_DEBTOR)) {
                if (data.equals(BotCallBackData.GO_HOME)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setMyOffset(0);
                    user.setStep(BotSteps.MAIN_MENU);
                    sendStoreOwnerMenu(user);
                    saveUserChanges(user);
                }
            } else if (user.getStep().equals(BotSteps.DEBT_MENU)) {
                if (data.equals(BotCallBackData.ADD_DEBT)) {
                    user.setStep(BotSteps.ADD_DEBT);
                    user.setMyOffset(0);
                    Debtor debtor = debtorRepository.findById(user.getDebtorId()).get();
                    String debtText = debtor.getDebtorFullName() + "\n" + debtor.getPhoneNumber() + "\n" +
                            debtRepository.getOneDebtorDebt(user.getStore().getId(), debtor.getId(), 0L, 0L, Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user)) + "\n" +
                            Lang.howMuchLoanGetFirstTime(user);
                    sendText(user, debtText);
                    deleteMsg(user, user.getContactBtnId());
                    saveUserChanges(user);
                } else if (data.equals(BotCallBackData.REDUCE_DEBT)) {
                    user.setMyOffset(0);
                    user.setStep(BotSteps.REDUCE_DEBT);
                    Debtor debtor = debtorRepository.findById(user.getDebtorId()).get();
                    UUID storeId = user.getStore().getId();
                    UUID debtorId = debtor.getId();
                    String debtText = debtor.getDebtorFullName() + "\n" + debtor.getPhoneNumber() + "\n" +
                            debtRepository.getOneDebtorDebt(storeId, debtorId, 0L,
                                    0L, Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user)) + "\n" + Lang.howMuchDebtPaid(user);
                    sendText(user, debtText);
                    user.setDebtorId(debtor.getId());
                    deleteMsg(user, user.getContactBtnId());
                    saveUserChanges(user);
                } else if (data.equals(BotCallBackData.CANCEL)) {
                    user.setMyOffset(0);
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.MAIN_MENU);
                    sendStoreOwnerMenu(user);
                    saveUserChanges(user);
                }
            } else if (user.getStep().equals(BotSteps.LIST_DEBTORS)) {
                String debtors = Lang.debtorText(user) + " :\n\n";
                List<DebtorProjection> storeDebtors = storeRepository.getStoreAllDebtors(user.getStore().getId());
                for (int i = 0; i < storeDebtors.size(); i++) {
                    debtors += i + 1 + " ) " + Lang.nameText(user) + " : " + storeDebtors.get(i).getFullName() + "\n" +
                            storeDebtors.get(i).getPhoneNumber() + "\n\n" + "********************\n";
                }

                List<ExcelDebtor> excelDebtors = storeRepository.getStoreAllDebtorsForCreateExcelFile(user.getStore().getId());

                if (data.equals(BotCallBackData.GO_HOME)) {
                    user.setStep(BotSteps.MAIN_MENU);
                    user.setMyOffset(0);
                    deleteMsg(user, user.getContactBtnId());
                    sendStoreOwnerMenu(user);
                    saveUserChanges(user);
                } else if (data.equals(BotCallBackData.EXCEL)) {
                    Workbook workbook = new XSSFWorkbook();

                    String[] columnsUz = {"Ismi", "Tel", "Qarzi", "To'langan qarzi", "Qoldi"};
                    String[] columnsRu = {"Имя", "Тел", "Долг", "Оплаченный долг", "Остался"};
                    String[] columnsEng = {"Name", "Phone", "Debt", "Left on debt"};


                    Sheet sheet = workbook.createSheet("Debtors");


                    for (int i = 0; i < excelDebtors.size(); i++) {

                        ExcelDebtor debtor = excelDebtors.get(i);

                        Row row = sheet.createRow(0);
                        Row row1 = sheet.createRow(i + 1);

                        Font headerFont = workbook.createFont();
                        headerFont.setBold(true);
                        headerFont.setFontHeightInPoints((short) 12);
                        headerFont.setColor(IndexedColors.RED.getIndex());

                        CellStyle headerCellStyle = workbook.createCellStyle();
                        headerCellStyle.setFont(headerFont);

                        if (user.getSelected_language().equals("uz")) {


                            for (int i1 = 0; i1 < columnsUz.length; i1++) {
                                Cell cell = row.createCell(i1);
                                cell.setCellValue(columnsUz[i1]);
                                cell.setCellStyle(headerCellStyle);
                            }

                            Cell cell = row1.createCell(0);
                            cell.setCellValue(debtor.getFullName());

                            Cell cell1 = row1.createCell(1);
                            cell1.setCellValue(debtor.getPhoneNumber());

                            Cell cell3 = row1.createCell(2);
                            cell3.setCellValue(debtor.getDebt());

                            Cell cell2 = row1.createCell(3);
                            cell2.setCellValue(debtor.getPaidDebt());


                            Cell cell4 = row1.createCell(4);
                            cell4.setCellValue(debtor.getRemainDebt());


                            for (int j = 0; j < columnsUz.length; j++) {
                                sheet.autoSizeColumn(j);
                            }
                        } else if (user.getSelected_language().equals("ru")) {
                            for (int i1 = 0; i1 < columnsRu.length; i1++) {
                                Cell cell = row.createCell(i1);
                                cell.setCellValue(columnsRu[i1]);
                                cell.setCellStyle(headerCellStyle);
                            }

                            Cell cell = row1.createCell(0);
                            cell.setCellValue(debtor.getFullName());

                            Cell cell1 = row1.createCell(1);
                            cell1.setCellValue(debtor.getPhoneNumber());

                            Cell cell3 = row1.createCell(2);
                            cell3.setCellValue(debtor.getDebt());

                            Cell cell2 = row1.createCell(3);
                            cell2.setCellValue(debtor.getPaidDebt());

                            Cell cell4 = row1.createCell(4);
                            cell4.setCellValue(debtor.getRemainDebt());

                            for (int j = 0; j < columnsRu.length; j++) {
                                sheet.autoSizeColumn(j);
                            }

                        } else {
                            for (int i1 = 0; i1 < columnsEng.length; i1++) {
                                Cell cell = row.createCell(i1);
                                cell.setCellValue(columnsEng[i1]);
                                cell.setCellStyle(headerCellStyle);
                            }

                            Cell cell = row1.createCell(0);
                            cell.setCellValue(debtor.getFullName());

                            Cell cell1 = row1.createCell(1);
                            cell1.setCellValue(debtor.getPhoneNumber());

                            Cell cell3 = row1.createCell(2);
                            cell3.setCellValue(debtor.getDebt());

                            Cell cell2 = row1.createCell(3);
                            cell2.setCellValue(debtor.getPaidDebt());

                            Cell cell4 = row1.createCell(4);
                            cell4.setCellValue(debtor.getRemainDebt());
                            for (int j = 0; j < columnsEng.length; j++) {
                                sheet.autoSizeColumn(j);
                            }

                        }
                    }

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    workbook.write(outputStream);
                    workbook.close();

                    byte[] bytes = outputStream.toByteArray();
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setDocument(new InputFile(inputStream, Lang.debtorText(user) + ".xlsx"));
                    sendDocument.setChatId(user.getChatId());
                    sendDocument.setReplyMarkup(BotUtils.goToHomeButton(user));
                    Message execute = execute(sendDocument);
                    user.setExcelMsgId(execute.getMessageId());
                    user.setStep(BotCallBackData.EXCEL);
                    saveUserChanges(user);
                    deleteMsg(user, user.getContactBtnId());
                } else if (data.equals(BotCallBackData.PREVIOUS)) {
                    user.setMyOffset(user.getMyOffset() - 1);
                    saveUserChanges(user);
                    String debtorsText = Lang.debtorText(user) + " :\n\n";

                    PageRequest pageRequest = PageRequest.of(user.getMyOffset(), 5);
                    Page<DebtorProjection> debtorsList = storeRepository.getStoreDebtors(user.getStore().getId(), pageRequest,
                            Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user));


                    for (int i = 0; i < debtorsList.getContent().size(); i++) {
                        debtorsText += i + 1 + " ) " + Lang.nameText(user) + " : " + debtorsList.getContent().get(i).getFullName() + "\n" +
                                debtorsList.getContent().get(i).getPhoneNumber() + "\n" + debtorsList.getContent().get(i).getDebt() + "\n \uD83D\uDD11 :  /" + debtorsList.getContent().get(i).getId().replace("-", "_") + "\n\n" + "******\n";
                    }


                    int i = storeDebtors.size() - debtorsList.getTotalPages() * 5;


                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setChatId(user.getChatId());
                    editMessageText.setText(debtorsText);
                    editMessageText.setMessageId(user.getContactBtnId());
                    if (user.getMyOffset() == 0) {
                        editMessageText.setReplyMarkup(BotUtils.generatePaginationNextButton(user));
                    } else if (user.getMyOffset() < debtorsList.getTotalPages() && user.getMyOffset() > 0) {
                        editMessageText.setReplyMarkup(BotUtils.generatePaginationButton(user));
                    }
                    execute(editMessageText);
                } else if (data.equals(BotCallBackData.NEXT)) {
                    user.setMyOffset(user.getMyOffset() + 1);
                    saveUserChanges(user);
                    String debtorsText = Lang.debtorText(user) + " :\n\n";

                    PageRequest pageRequest = PageRequest.of(user.getMyOffset(), 5);
                    Page<DebtorProjection> debtorsList = storeRepository.getStoreDebtors(user.getStore().getId(), pageRequest,
                            Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user));


                    for (int i = 0; i < debtorsList.getContent().size(); i++) {
                        debtorsText += i + 1 + " ) " + Lang.nameText(user) + " : " + debtorsList.getContent().get(i).getFullName() + "\n" +
                                debtorsList.getContent().get(i).getPhoneNumber() + "\n" + debtorsList.getContent().get(i).getDebt() + "\n \uD83D\uDD11 :  /" + debtorsList.getContent().get(i).getId().replace("-", "_") + "\n\n" + "******\n";
                    }


                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setChatId(user.getChatId());
                    editMessageText.setText(debtorsText);
                    editMessageText.setMessageId(user.getContactBtnId());
                    if (user.getMyOffset() + 2 <= debtorsList.getTotalPages() && user.getMyOffset() > 0) {
                        editMessageText.setReplyMarkup(BotUtils.generatePaginationButton(user));
                    } else if (debtorsList.getTotalPages() == user.getMyOffset() + 1) {
                        editMessageText.setReplyMarkup(BotUtils.generatePaginationPrevButton(user));
                    }
                    execute(editMessageText);
                } else if (data.equals(BotCallBackData.CANCEL)) {
                    deleteMsg(user, user.getContactBtnId());

                    user.setMyOffset(user.getMyOffset());
                    saveUserChanges(user);
                    String debtorsText = Lang.debtorText(user) + " :\n\n";

                    PageRequest pageRequest = PageRequest.of(user.getMyOffset(), 5);
                    Page<DebtorProjection> debtorsList = storeRepository.getStoreDebtors(user.getStore().getId(), pageRequest,
                            Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user), Lang.leftOverText(user));


                    for (int i = 0; i < debtorsList.getContent().size(); i++) {
                        debtorsText += i + 1 + " ) " + Lang.nameText(user) + " : " + debtorsList.getContent().get(i).getFullName() + "\n" +
                                debtorsList.getContent().get(i).getPhoneNumber() + "\n" + debtorsList.getContent().get(i).getDebt() + "\n \uD83D\uDD11 :  /" + debtorsList.getContent().get(i).getId().replace("-", "_") + "\n\n" + "******\n";
                    }

                    if (storeDebtors.size() > 5) {
                        sendInlineButtonMsg(user, debtorsText, BotUtils.generatePaginationNextButton(user));
                    } else {
                        sendInlineButtonMsg(user, debtorsText, BotUtils.generateExcelButton(user));
                    }

                }
            } else if (user.getStep().equals(BotCallBackData.EXCEL)) {
                if (data.equals(BotCallBackData.GO_HOME)) {
                    user.setMyOffset(0);
                    user.setStep(BotSteps.MAIN_MENU);
                    sendStoreOwnerMenu(user);
                    deleteMsg(user, user.getExcelMsgId());
                    saveUserChanges(user);
                }
            } else if (user.getStep().equals(BotSteps.ADD_DEBT_CONFIRMATION)) {
                if (data.equals(BotCallBackData.CONFIRM)) {
                    user.setMyOffset(0);
                    deleteMsg(user, user.getContactBtnId());
                    Debtor debtor = debtorRepository.findById(user.getDebtorId()).get();

                    Store store = user.getStore();

                    Debt debt = new Debt();
                    debt.setDebtorDebt(store.getConfirmationDebt());
                    debt.setLeftOver(store.getConfirmationDebt());
                    debt.setActive(true);
                    debt.setDebtor(debtor);
                    debt.setStore(store);
                    debtRepository.save(debt);

                    List<Store> debtor_stores = new ArrayList<>();
                    debtor_stores.add(store);
                    debtor.setStores(debtor_stores);
                    debtorRepository.save(debtor);


                    user.setStep(BotSteps.MAIN_MENU);
                    sendStoreOwnerMenu(user);
                    saveUserChanges(user);
                } else if (data.equals(BotCallBackData.CANCEL)) {
                    user.setMyOffset(0);
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.MAIN_MENU);
                    sendStoreOwnerMenu(user);
                    saveUserChanges(user);
                }
            } else if (user.getStep().equals(BotSteps.REDUCE_DEBT_CONFIRMATION)) {
                if (data.equals(BotCallBackData.CONFIRM)) {
                    deleteMsg(user, user.getContactBtnId());
                    user.setMyOffset(0);
                    user.setStep(BotSteps.MAIN_MENU);
                    sendStoreOwnerMenu(user);
                    Store store = user.getStore();
                    Debtor debtor = debtorRepository.findById(user.getDebtorId()).get();
                    List<Debt> debtorDebts = debtRepository.getDebtorDebts(debtor.getId());
                    Long remainder = store.getConfirmationDebt();
                    for (int i = 0; i < debtorDebts.size(); i++) {
                        if (debtorDebts.get(i).getLeftOver() >= remainder) {
                            Payment payment = new Payment();

                            if (debtorDebts.get(i).getLeftOver() == remainder) {
                                debtorDebts.get(i).setActive(false);
                            }

                            payment.setAmount(remainder);
                            payment.setDebt(debtorDebts.get(i));
                            paymentRepository.save(payment);

                            debtorDebts.get(i).setLeftOver(debtorDebts.get(i).getLeftOver() - remainder);
                            debtRepository.save(debtorDebts.get(i));
                            remainder = 0L;
                            for (Debt debt : debtorDebts) {
                                if (debt.getLeftOver() == 0) {
                                    debt.setActive(false);
                                    debtRepository.save(debt);
                                }
                            }

                            return;
                        } else {
                            Payment payment = new Payment();
                            payment.setAmount(store.getConfirmationDebt());
                            payment.setDebt(debtorDebts.get(i));

                            remainder = remainder - debtorDebts.get(i).getLeftOver();
                            debtorDebts.get(i).setLeftOver(0L);
                            debtorDebts.get(i).setActive(false);
                            paymentRepository.save(payment);
                            debtRepository.save(debtorDebts.get(i));
                            for (Debt debt : debtorDebts) {
                                if (debt.getLeftOver() == 0) {
                                    debt.setActive(false);
                                    debtRepository.save(debt);
                                }
                            }

                        }
                    }
                    for (Debt debt : debtorDebts) {
                        if (debt.getLeftOver() == 0) {
                            debt.setActive(false);
                            debtRepository.save(debt);
                        }
                    }
                    saveUserChanges(user);
                } else if (data.equals(BotCallBackData.CANCEL)) {
                    user.setMyOffset(0);
                    deleteMsg(user, user.getContactBtnId());
                    user.setStep(BotSteps.MAIN_MENU);
                    sendStoreOwnerMenu(user);
                    saveUserChanges(user);
                }
            }
        } else if (update.hasInlineQuery()) {

            BotUser user = getUser(update.getInlineQuery().getFrom().getId().toString());

            if (user.getStep().equals(BotSteps.LIST_DEBTORS) || user.getStep().equals(BotSteps.MAIN_MENU)) {
                handleIncomingInlineQuery(update.getInlineQuery(), user);
                String query = update.getInlineQuery().getQuery();
                if (!query.isEmpty()) {
                    user.setMyQuery(query);
                    saveUserChanges(user);
                }
            } else if (user.getStep().equals(BotSteps.CHANGE_DEBTOR)) {
                handleIncomingInlineQuery1(update.getInlineQuery(), user);
            }


        }


    }

    @SneakyThrows
    private void updateMsg(BotUser user, Integer msgId, String text,
                           InlineKeyboardMarkup inlineKeyboardMarkup) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setText(text);
        editMessageText.setChatId(user.getChatId());
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        editMessageText.setMessageId(msgId);
        execute(editMessageText);
    }

    @SneakyThrows
    public void sendMessageToStoreOwners(Attachment attachment,
                                         String description,
                                         String chatId) {

        SendPhoto sendPhoto = new SendPhoto();
        InputFile inputFile = new InputFile();
        InputStream targetStream = new ByteArrayInputStream(attachment.getFile());
        inputFile.setMedia(targetStream, attachment.getId().toString());

        sendPhoto.setPhoto(inputFile);
        sendPhoto.setCaption(description);
        sendPhoto.setChatId(chatId);
        execute(sendPhoto);
    }

    @SneakyThrows
    private void handleIncomingInlineQuery(InlineQuery inlineQuery, BotUser user) {
        String query = inlineQuery.getQuery();
        if (!query.isEmpty()) {
            List<CustomDebtor> debtors = debtorRepository.getInlineQueryResult(
                    user.getStore().getId(),
                    query, Lang.debtRepoTxt(user),
                    Lang.paidDebtRepoTxt(user)
            );
            execute(convertResultsToResponse(inlineQuery, debtors, user));
        } else {
            execute(convertResultsToResponse(inlineQuery, new ArrayList<>(), user));

        }
    }

    @SneakyThrows
    private void handleIncomingInlineQuery1(InlineQuery inlineQuery, BotUser user) {
        String query = inlineQuery.getQuery();
        if (!query.isEmpty()) {
            List<CustomDebtor> debtors = debtorRepository.getInlineQueryResult(user.getStore().getId(),
                    query, Lang.debtRepoTxt(user), Lang.paidDebtRepoTxt(user));
            execute(convertResultsToResponse1(inlineQuery, debtors, user));
        } else {
            execute(convertResultsToResponse1(inlineQuery, new ArrayList<>(), user));
        }
    }

    private AnswerInlineQuery convertResultsToResponse(InlineQuery inlineQuery,
                                                       List<CustomDebtor> debtors, BotUser user) {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setCacheTime(CACHETIME);
        answerInlineQuery.setResults(convertRaeResults(debtors, user));
        return answerInlineQuery;
    }


    //

    private AnswerInlineQuery convertResultsToResponse1(InlineQuery inlineQuery,
                                                        List<CustomDebtor> debtors, BotUser user) {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setCacheTime(CACHETIME);
        answerInlineQuery.setResults(convertRaeResults1(debtors, user));
        return answerInlineQuery;
    }


    private List<InlineQueryResult> convertRaeResults(List<CustomDebtor> debtors, BotUser user) {
        List<InlineQueryResult> results = new ArrayList<>();


        if (debtors.size() == 0) {
            InputTextMessageContent messageContent = new InputTextMessageContent();
            messageContent.setDisableWebPagePreview(true);
            messageContent.setMessageText("/newDebtor");
            InlineQueryResultArticle article = new InlineQueryResultArticle();
            article.setInputMessageContent(messageContent);
            article.setId(Integer.toString(1));
            article.setTitle(Lang.wYLAND(user));
//            article.setDescription(Lang.wYLAND(user));
            results.add(article);
        } else {
            for (int i = 0; i < debtors.size(); i++) {
                CustomDebtor customDebtor = debtors.get(i);
                InputTextMessageContent messageContent = new InputTextMessageContent();
                messageContent.setDisableWebPagePreview(true);
                messageContent.setMessageText("/" + customDebtor.getId().toString().replace("-", "_"));
                InlineQueryResultArticle article = new InlineQueryResultArticle();
                article.setInputMessageContent(messageContent);
                article.setId(Integer.toString(i));
                article.setTitle(customDebtor.getFullName());
                article.setDescription(customDebtor.getPhoneNumber() + "\n" + customDebtor.getDebt());
                results.add(article);
            }
        }

        return results;
    }


    //
    private List<InlineQueryResult> convertRaeResults1(List<CustomDebtor> debtors, BotUser user) {
        List<InlineQueryResult> results = new ArrayList<>();


        if (debtors.size() != 0) {
            for (int i = 0; i < debtors.size(); i++) {
                CustomDebtor customDebtor = debtors.get(i);
                InputTextMessageContent messageContent = new InputTextMessageContent();
                messageContent.setDisableWebPagePreview(true);
                messageContent.setMessageText("/" + customDebtor.getId().toString().replace("-", "_"));
                InlineQueryResultArticle article = new InlineQueryResultArticle();
                article.setInputMessageContent(messageContent);
                article.setId(Integer.toString(i));
                article.setTitle(customDebtor.getFullName());
                article.setDescription(customDebtor.getPhoneNumber() + "\n" + customDebtor.getDebt());
                results.add(article);
            }
        }

        return results;
    }

    private void saveDebtorChanges(Debtor debtor) {
        debtorRepository.save(debtor);
    }

    @SneakyThrows
    private void sendStoreOwnerMenu(BotUser user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setText(Lang.selectMenu(user));
        sendMessage.setReplyMarkup(BotUtils.generateMainMenuButtons(user,
                storeRepository.getStoreAllDebtors(user.getStore().getId()).size()));
        Message execute = execute(sendMessage);
        user.setMenuBntId(execute.getMessageId());
        saveUserChanges(user);
    }

    @SneakyThrows
    private void reportInformationByTimes(BotUser user) {

        UUID storeId = user.getStore().getId();

        String reportText = "<b><i>" + Lang.reportText(user) + "</i></b>\n\n" +
                Lang.nameText(user) + " : " + user.getFullName() + "\n" +
                Lang.reportStoreNameText(user) + user.getStore().getStoreName() + "\n" +
                Lang.numberText(user) + user.getPhoneNumber() + "\n\n" +
                Lang.reportTodayText(user) + "   \n" + storeRepository.getTodayReportDebt(storeId, Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user)) + "\n\n" +
                Lang.reportYesterdayText(user) + "   \n" + storeRepository.getYesterdayReportDebt(storeId, Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user)) + "\n\n" +
                Lang.reportWeeklyText(user) + "   \n" + storeRepository.getWeeklyReportDebt(storeId, Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user)) + "\n\n" +
                Lang.reportMonthlyText(user) + "   \n" + storeRepository.getMonthlyReportDebt(storeId, Lang.paidDebtRepoTxt(user), Lang.debtRepoTxt(user));


        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setText(reportText);
        sendMessage.setReplyMarkup(BotUtils.seeAllDebtorsList(user));
        Message execute = execute(sendMessage);
        user.setStartMsgId(execute.getMessageId());
        saveUserChanges(user);
    }

    @SneakyThrows
    private void sendInlineButtonMsg(BotUser user, String text,
                                     InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setParseMode(ParseMode.HTML);
        Message execute = execute(sendMessage);
        user.setContactBtnId(execute.getMessageId());
        saveUserChanges(user);
    }


    @SneakyThrows
    private void submitYourStoreLocation(BotUser user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setText(Lang.sendLocationOfStore(user));
        sendMessage.setReplyMarkup(BotUtils.generateLocationButton(user));
        Message execute = execute(sendMessage);
        user.setLocationBtnId(execute.getMessageId());
        saveUserChanges(user);
    }

    @SneakyThrows
    private void sendText(BotUser user, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setText(text);
        Message execute = execute(sendMessage);
        user.setMsgId(execute.getMessageId());
        saveUserChanges(user);
    }


    @SneakyThrows
    private void enterYourFullNameRequest(BotUser user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(Lang.enterYourFullNameRequestText(user));
        sendMessage.setChatId(user.getChatId());
        Message execute = execute(sendMessage);
        user.setLangBntId(execute.getMessageId());
        saveUserChanges(user);
    }


    @SneakyThrows
    private void sendMenuToSuperUser(BotUser user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setText(Lang.selectMenu(user));
        sendMessage.setReplyMarkup(BotUtils.generateSuperAdminMainMenuButtons(user));
        Message execute = execute(sendMessage);
        user.setStartMsgId(execute.getMessageId());
        saveUserChanges(user);
    }


    @SneakyThrows
    private void deleteMsg(BotUser user, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(user.getChatId());
        deleteMessage.setMessageId(messageId);
        execute(deleteMessage);
    }

    @SneakyThrows
    private void sendRequestContactToUser(BotUser user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setText(Lang.generateContactBtnText(user));
        sendMessage.setReplyMarkup(BotUtils.generateContactButton(user));
        Message execute = execute(sendMessage);
        user.setContactBtnId(execute.getMessageId());
        saveUserChanges(user);
    }

    @SneakyThrows
    private void changeOwnerContactRequest(BotUser user) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setText(Lang.changeOwnerContactTxt(user));
        sendMessage.setReplyMarkup(BotUtils.generateContactButton(user));
        Message execute = execute(sendMessage);
        user.setStartMsgId(execute.getMessageId());
        saveUserChanges(user);
    }

    private void saveUserChanges(BotUser user) {
        userRepository.save(user);
    }

    @SneakyThrows
    private void gatheringLanguage(BotUser user, User from) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getChatId());
        sendMessage.setText(Lang.gatheringBotStart(from));
        sendMessage.setReplyMarkup(BotUtils.generateGatheringLanguageButtons());
        Message execute = execute(sendMessage);
        user.setLangBntId(execute.getMessageId());
        user.setStep(BotSteps.SELECT_LANG);
        saveUserChanges(user);
    }

    private BotUser getUser(String chatId) {
        return userRepository.findByChatId(chatId)
                .orElseGet(() -> userRepository.save(new BotUser(chatId)));
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}