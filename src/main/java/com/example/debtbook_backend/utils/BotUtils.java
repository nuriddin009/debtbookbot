package com.example.debtbook_backend.utils;

import com.example.debtbook_backend.entity.BotUser;
import com.example.debtbook_backend.entity.DebtUser;
import com.example.debtbook_backend.entity.Debtor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;


import java.util.LinkedList;
import java.util.List;

public interface BotUtils {


    static InlineKeyboardMarkup generateGatheringLanguageButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> td = new LinkedList<>();
        InlineKeyboardButton uzButton = new InlineKeyboardButton();
        uzButton.setText("\uD83C\uDDFA\uD83C\uDDFF o'z");
        uzButton.setCallbackData("uz");
        InlineKeyboardButton ruButton = new InlineKeyboardButton();
        ruButton.setText("\uD83C\uDDF7\uD83C\uDDFA ру");
        ruButton.setCallbackData("ru");
        InlineKeyboardButton engButton = new InlineKeyboardButton();
        engButton.setText("\uD83C\uDDFA\uD83C\uDDF8 eng");
        engButton.setCallbackData("eng");
        td.add(uzButton);
        td.add(ruButton);
//        td.add(engButton);
        List<List<InlineKeyboardButton>> tr = new LinkedList<>();
        tr.add(td);
        inlineKeyboardMarkup.setKeyboard(tr);
        return inlineKeyboardMarkup;
    }


    static ReplyKeyboardMarkup generateContactButton(BotUser user) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText(Lang.generateContactBtnText(user));
        keyboardButton.setRequestContact(true);
        keyboardRow.add(keyboardButton);
        List<KeyboardRow> rows = new LinkedList<>();
        rows.add(keyboardRow);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rows);
        return replyKeyboardMarkup;
    }


    static ReplyKeyboardMarkup generateSuperAdminMainMenuButtons(BotUser user) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new LinkedList<>();

        // 1-row
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton();
        button1.setText(Lang.storesBtnText(user));

        KeyboardButton button2 = new KeyboardButton();
        button2.setText(Lang.seeCustomersBtnText(user));

        // 2-row
        KeyboardRow row2 = new KeyboardRow();

//        KeyboardButton button3 = new KeyboardButton();
//        button3.setText(Lang.addStoreBtnText(user));

        KeyboardButton button4 = new KeyboardButton();
        button4.setText(Lang.settingsFromUSer(user));


        row1.add(button1);
        row1.add(button2);

//        row2.add(button3);
        row2.add(button4);

        rows.add(row1);
        rows.add(row2);
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }


    static ReplyKeyboardMarkup generateLocationButton(BotUser user) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText(Lang.sendMyLocationBtnText(user));
        keyboardButton.setRequestLocation(true);
        keyboardRow.add(keyboardButton);
        List<KeyboardRow> rows = new LinkedList<>();
        rows.add(keyboardRow);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rows);
        return replyKeyboardMarkup;
    }

    static InlineKeyboardMarkup generateSuccessfullyRegisteredButton(BotUser user) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        List<InlineKeyboardButton> row = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Lang.useBotInstruction(user));
        button.setUrl("https://youtu.be/CymRDgsfBMM");
        button.setCallbackData("Bla bla bla");

        InlineKeyboardButton startWorking = new InlineKeyboardButton();
        startWorking.setText(Lang.startWorkingBtnText(user));
        startWorking.setCallbackData(BotSteps.START_WORKING);


        row.add(button);
        row.add(startWorking);
        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    static InlineKeyboardMarkup seeAllDebtorsList(BotUser user) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();


        List<InlineKeyboardButton> row1 = new LinkedList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setCallbackData(BotCallBackData.GO_HOME);
        button1.setText(Lang.goToHomeMenu(user));

        row1.add(button1);
        rows.add(row1);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    static InlineKeyboardMarkup goToHomeButton(BotUser user) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        List<InlineKeyboardButton> row1 = new LinkedList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setCallbackData(BotCallBackData.GO_HOME);
        button1.setText(Lang.goToHomeMenu(user));
        row1.add(button1);
        rows.add(row1);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    static InlineKeyboardMarkup cancelButton(BotUser user) {
        InlineKeyboardMarkup inlineKeyboardMarkup1 = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        List<InlineKeyboardButton> row = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData(BotCallBackData.GO_HOME);
        button.setText(Lang.cancelBtn(user));
        row.add(button);
        rowList.add(row);
        inlineKeyboardMarkup1.setKeyboard(rowList);
        return inlineKeyboardMarkup1;
    }

    static InlineKeyboardMarkup backBtn(BotUser user) {
        InlineKeyboardMarkup inlineKeyboardMarkup1 = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        List<InlineKeyboardButton> row = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData(BotCallBackData.GO_HOME);
        button.setText(Lang.backTxt(user));
        row.add(button);
        rowList.add(row);
        inlineKeyboardMarkup1.setKeyboard(rowList);
        return inlineKeyboardMarkup1;
    }


    static InlineKeyboardMarkup tryAgainButton(BotUser user) {
        InlineKeyboardMarkup inlineKeyboardMarkup1 = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        List<InlineKeyboardButton> row = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setCallbackData(BotCallBackData.CANCEL);
        button.setText(Lang.tryAgainButtonText(user));
        row.add(button);
        rowList.add(row);
        inlineKeyboardMarkup1.setKeyboard(rowList);
        return inlineKeyboardMarkup1;
    }


    static InlineKeyboardMarkup generateMainMenuButtons(BotUser user, int size) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        List<InlineKeyboardButton> row = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Lang.getReportText(user));
        button.setCallbackData(BotCallBackData.GET_REPORT);

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(Lang.addDebtorText(user));
//        button1.setCallbackData(BotCallBackData.ADD_DEBTOR);
        button1.setSwitchInlineQueryCurrentChat("");

        List<InlineKeyboardButton> rowHelp = new LinkedList<>();
        InlineKeyboardButton buttonHelp = new InlineKeyboardButton();
        buttonHelp.setText(Lang.useBotInstruction(user));
        buttonHelp.setUrl("https://youtu.be/CymRDgsfBMM");

        InlineKeyboardButton settings = new InlineKeyboardButton();
        settings.setText(Lang.settingsFromUSer(user));
        settings.setCallbackData(BotCallBackData.SETTINGS);

        List<InlineKeyboardButton> debtorListButtons = new LinkedList<>();
        InlineKeyboardButton allDebtors = new InlineKeyboardButton();
        allDebtors.setText(Lang.seeAllDebtorsListText(user));
        allDebtors.setCallbackData(BotCallBackData.LIST_DEBTORS);


        row.add(button);
        row.add(button1);
        rowHelp.add(settings);
        rowHelp.add(buttonHelp);
        debtorListButtons.add(allDebtors);

        rows.add(row);
        rows.add(rowHelp);
        if (size != 0) {
            rows.add(debtorListButtons);
        }

        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    static InlineKeyboardMarkup addDecDebtButtons(BotUser user) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        List<InlineKeyboardButton> row1 = new LinkedList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setCallbackData(BotCallBackData.ADD_DEBT);
        button1.setText(Lang.addDebtToDebtor(user));

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setCallbackData(BotCallBackData.REDUCE_DEBT);
        button2.setText(Lang.reduceDebtFromDebtor(user));


        List<InlineKeyboardButton> row2 = new LinkedList<>();

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText(Lang.cancelBtn(user));
        button3.setCallbackData(BotCallBackData.CANCEL);


        row1.add(button1);
        row1.add(button2);
        row2.add(button3);
        rows.add(row1);
        rows.add(row2);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    static InlineKeyboardMarkup addDebtButtons(BotUser user) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        List<InlineKeyboardButton> row1 = new LinkedList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setCallbackData(BotCallBackData.ADD_DEBT);
        button1.setText(Lang.addDebtToDebtor(user));

        row1.add(button1);
        rows.add(row1);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    static InlineKeyboardMarkup generateExcelButton(BotUser user) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        List<InlineKeyboardButton> row = new LinkedList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Lang.excelText(user));
        button.setCallbackData(BotCallBackData.EXCEL);

        List<InlineKeyboardButton> row1 = new LinkedList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(Lang.goToHomeMenu(user));
        button1.setCallbackData(BotCallBackData.GO_HOME);


        row.add(button);
        row1.add(button1);
        rows.add(row);
        rows.add(row1);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    static InlineKeyboardMarkup generatePaginationButton(BotUser user) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();

        List<InlineKeyboardButton> row01 = new LinkedList<>();
        InlineKeyboardButton prev = new InlineKeyboardButton();
        prev.setText("⏪");
        prev.setCallbackData(BotCallBackData.PREVIOUS);
        InlineKeyboardButton nextBtn = new InlineKeyboardButton();
        nextBtn.setText("⏩");
        nextBtn.setCallbackData(BotCallBackData.NEXT);

        List<InlineKeyboardButton> row = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Lang.excelText(user));
        button.setCallbackData(BotCallBackData.EXCEL);

        InlineKeyboardButton searchB = new InlineKeyboardButton();
        searchB.setText(Lang.searchText(user));
        searchB.setSwitchInlineQueryCurrentChat("");


        List<InlineKeyboardButton> row1 = new LinkedList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(Lang.goToHomeMenu(user));
        button1.setCallbackData(BotCallBackData.GO_HOME);


        row01.add(prev);
        row01.add(nextBtn);
        row.add(button);
        row.add(searchB);
        row1.add(button1);
        rows.add(row01);
        rows.add(row);
        rows.add(row1);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    static InlineKeyboardMarkup generateSettingsPaginationButton(BotUser user) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();

        List<InlineKeyboardButton> row01 = new LinkedList<>();
        InlineKeyboardButton prev = new InlineKeyboardButton();
        prev.setText("⏪");
        prev.setCallbackData(BotCallBackData.PREVIOUS);
        InlineKeyboardButton nextBtn = new InlineKeyboardButton();
        nextBtn.setText("⏩");
        nextBtn.setCallbackData(BotCallBackData.NEXT);

        List<InlineKeyboardButton> row = new LinkedList<>();

        InlineKeyboardButton searchB = new InlineKeyboardButton();
        searchB.setText(Lang.searchText(user));
        searchB.setSwitchInlineQueryCurrentChat("");

        List<InlineKeyboardButton> row1 = new LinkedList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(Lang.backTxt(user));
        button1.setCallbackData(BotCallBackData.GO_HOME);

        row01.add(prev);
        row01.add(nextBtn);
        row.add(searchB);
        row1.add(button1);
        rows.add(row01);
        rows.add(row);
        rows.add(row1);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    static InlineKeyboardMarkup generatePaginationNextButton(BotUser user) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();

        List<InlineKeyboardButton> row01 = new LinkedList<>();
        InlineKeyboardButton nextBtn = new InlineKeyboardButton();
        nextBtn.setText("⏩");
        nextBtn.setCallbackData(BotCallBackData.NEXT);

        List<InlineKeyboardButton> row = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Lang.excelText(user));
        button.setCallbackData(BotCallBackData.EXCEL);

        InlineKeyboardButton searchB = new InlineKeyboardButton();
        searchB.setText(Lang.searchText(user));
        searchB.setSwitchInlineQueryCurrentChat("");

        List<InlineKeyboardButton> row1 = new LinkedList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(Lang.goToHomeMenu(user));
        button1.setCallbackData(BotCallBackData.GO_HOME);
        row01.add(nextBtn);
        row.add(button);
        row.add(searchB);
        row1.add(button1);
        rows.add(row01);
        rows.add(row);
        rows.add(row1);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    static InlineKeyboardMarkup generateSettingsPaginationNextButton(BotUser user) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();

        List<InlineKeyboardButton> row01 = new LinkedList<>();
        InlineKeyboardButton nextBtn = new InlineKeyboardButton();
        nextBtn.setText("⏩");
        nextBtn.setCallbackData(BotCallBackData.NEXT);

        List<InlineKeyboardButton> row = new LinkedList<>();

        InlineKeyboardButton searchB = new InlineKeyboardButton();
        searchB.setText(Lang.searchText(user));
        searchB.setSwitchInlineQueryCurrentChat("");

        List<InlineKeyboardButton> row1 = new LinkedList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(Lang.backTxt(user));
        button1.setCallbackData(BotCallBackData.GO_HOME);
        row01.add(nextBtn);
        row.add(searchB);
        row1.add(button1);
        rows.add(row01);
        rows.add(row);
        rows.add(row1);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    static InlineKeyboardMarkup generateSettingsPaginationPrevButton(BotUser user) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();

        List<InlineKeyboardButton> row01 = new LinkedList<>();
        InlineKeyboardButton prev = new InlineKeyboardButton();
        prev.setText("⏪");
        prev.setCallbackData(BotCallBackData.PREVIOUS);

        List<InlineKeyboardButton> row = new LinkedList<>();

        InlineKeyboardButton searchB = new InlineKeyboardButton();
        searchB.setText(Lang.searchText(user));
        searchB.setSwitchInlineQueryCurrentChat("");


        List<InlineKeyboardButton> row1 = new LinkedList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(Lang.backTxt(user));
        button1.setCallbackData(BotCallBackData.GO_HOME);

        row01.add(prev);

        row.add(searchB);
        row1.add(button1);
        rows.add(row01);
        rows.add(row);
        rows.add(row1);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    static InlineKeyboardMarkup generatePaginationPrevButton(BotUser user) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();

        List<InlineKeyboardButton> row01 = new LinkedList<>();
        InlineKeyboardButton prev = new InlineKeyboardButton();
        prev.setText("⏪");
        prev.setCallbackData(BotCallBackData.PREVIOUS);

        List<InlineKeyboardButton> row = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Lang.excelText(user));
        button.setCallbackData(BotCallBackData.EXCEL);


        InlineKeyboardButton searchB = new InlineKeyboardButton();
        searchB.setText(Lang.searchText(user));
        searchB.setSwitchInlineQueryCurrentChat("");


        List<InlineKeyboardButton> row1 = new LinkedList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(Lang.goToHomeMenu(user));
        button1.setCallbackData(BotCallBackData.GO_HOME);

        row01.add(prev);

        row.add(button);
        row.add(searchB);
        row1.add(button1);
        rows.add(row01);
        rows.add(row);
        rows.add(row1);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    static InlineKeyboardMarkup confirmOrCancelButtons(BotUser user) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        List<InlineKeyboardButton> row = new LinkedList<>();
        InlineKeyboardButton confirmBnt = new InlineKeyboardButton();
        confirmBnt.setText(Lang.confirmText(user));
        confirmBnt.setCallbackData(BotCallBackData.CONFIRM);


        InlineKeyboardButton cancelBnt = new InlineKeyboardButton();
        cancelBnt.setText(Lang.cancelBtn(user));
        cancelBnt.setCallbackData(BotCallBackData.CANCEL);


        row.add(confirmBnt);
        row.add(cancelBnt);
        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    static InlineKeyboardMarkup seeMyDebtButton(DebtUser debtor) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        List<InlineKeyboardButton> row = new LinkedList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Lang.seeDebtBntText(debtor));
        button.setCallbackData(BotCallBackData.MY_DEBT);


        row.add(button);
        rows.add(row);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    static InlineKeyboardMarkup settingsMenuButtons(BotUser user, int size) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        List<InlineKeyboardButton> row = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Lang.changeLang(user));
        button.setCallbackData(BotCallBackData.CHANGE_LANG);

        List<InlineKeyboardButton> rowStore = new LinkedList<>();
        InlineKeyboardButton store = new InlineKeyboardButton();
        store.setText(Lang.changeStoreInfo(user));
        store.setCallbackData(BotCallBackData.CHANGE_STORE);


        List<InlineKeyboardButton> rowDebtor = new LinkedList<>();
        InlineKeyboardButton debtor = new InlineKeyboardButton();
        debtor.setText(Lang.changeDebtorInfo(user));
        debtor.setCallbackData(BotCallBackData.CHANGE_DEBTOR);

        List<InlineKeyboardButton> row01 = new LinkedList<>();
        InlineKeyboardButton inlineButton = new InlineKeyboardButton();
        inlineButton.setText(Lang.ownCabinetTxt(user));
        inlineButton.setCallbackData(BotCallBackData.CABINET);

        List<InlineKeyboardButton> rowEnd = new LinkedList<>();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText(Lang.goToHomeMenu(user));
        keyboardButton.setCallbackData(BotCallBackData.GO_HOME);


        row01.add(inlineButton);
        rowStore.add(store);
        rowDebtor.add(debtor);
        rowEnd.add(keyboardButton);

        row.add(button);
        rows.add(row);
        rows.add(rowStore);
        if (size != 0) {
            rows.add(rowDebtor);
        }
        rows.add(row01);
        rows.add(rowEnd);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    static InlineKeyboardMarkup changeStoreInformationButtons(BotUser user) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();

        List<InlineKeyboardButton> row = new LinkedList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Lang.changeStoreName(user));
        button.setCallbackData(BotCallBackData.STORE_NAME);

        List<InlineKeyboardButton> row1 = new LinkedList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(Lang.changeStoreLocation(user));
        button1.setCallbackData(BotCallBackData.STORE_LOCATION);

        List<InlineKeyboardButton> rowEnd = new LinkedList<>();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText(Lang.backTxt(user));
        keyboardButton.setCallbackData(BotCallBackData.CANCEL);

        rowEnd.add(keyboardButton);
        row1.add(button1);
        row.add(button);
        rows.add(row);
        rows.add(row1);
        rows.add(rowEnd);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }


    static InlineKeyboardMarkup ownerMenuButtons(BotUser user) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        List<InlineKeyboardButton> row = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Lang.ownerNameTxt(user));
        button.setCallbackData(BotCallBackData.OWNER_NAME);

        List<InlineKeyboardButton> row1 = new LinkedList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(Lang.ownerPhoneTxt(user));
        button1.setCallbackData(BotCallBackData.OWNER_PHONE);

        List<InlineKeyboardButton> rowEnd = new LinkedList<>();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText(Lang.backTxt(user));
        keyboardButton.setCallbackData(BotCallBackData.CANCEL);


        row.add(button);
        row1.add(button1);
        rowEnd.add(keyboardButton);
        rows.add(row);
        rows.add(row1);
        rows.add(rowEnd);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    static InlineKeyboardMarkup changeDebtorMenuButton(BotUser user, Debtor debtor) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        List<InlineKeyboardButton> row = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Lang.debtorNameTxt(user, debtor));
        button.setCallbackData(BotCallBackData.DEBTOR_NAME);

        List<InlineKeyboardButton> row1 = new LinkedList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(Lang.debtorPhoneTxt(user, debtor));
        button1.setCallbackData(BotCallBackData.DEBTOR_PHONE);

        List<InlineKeyboardButton> rowEnd = new LinkedList<>();
        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText(Lang.backTxt(user));
        keyboardButton.setCallbackData(BotCallBackData.CANCEL);


        rowEnd.add(keyboardButton);
        row.add(button);
        row1.add(button1);
        rows.add(row);
        rows.add(row1);
        rows.add(rowEnd);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    static InlineKeyboardMarkup addDebtBtn(BotUser user) {


        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new LinkedList<>();
        List<InlineKeyboardButton> row = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(Lang.addDebtToDebtor(user));
        button.setCallbackData(BotCallBackData.ADD_DEBT);

        List<InlineKeyboardButton> row1 = new LinkedList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(Lang.cancelBtn(user));
        button1.setCallbackData(BotCallBackData.CANCEL);


        row1.add(button1);
        row.add(button);
        rows.add(row);
        rows.add(row1);
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

}
