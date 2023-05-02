package com.example.debtbook_backend.utils;

import com.example.debtbook_backend.entity.BotUser;
import com.example.debtbook_backend.entity.DebtUser;
import com.example.debtbook_backend.entity.Debtor;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.example.debtbook_backend.utils.Variables.*;

public interface Lang {


    static String gatheringBotStart(User from) {
        return "\uD83C\uDDFA\uD83C\uDDFF Assalamu alaykum " + from.getFirstName()
                + "! Botimizga xush kelibsiz.\n" +
                "Botdan foydalanish uchun iltimos o'zingizga kerakli tilni tanlang! \n" +
                "\uD83C\uDDF7\uD83C\uDDFA Здравствуйте " + from.getFirstName() + "! Добро пожаловать в наш бот.\n" +
                "Чтобы использовать бот, выберите язык!";
//        +
//                "\uD83C\uDDFA\uD83C\uDDF8 Hello " + from.getFirstName() + "! Welcome to our bot.\n" +
//                "To use the bot, please choose the language you want!"
    }


    static String generateContactBtnText(BotUser user) {
        return user.getSelected_language().equals("uz") ? SEND_YOUR_CONTACT_UZ
                : user.getSelected_language().equals("ru") ? SEND_YOUR_CONTACT_RU
                : SEND_YOUR_CONTACT_ENG;
    }

    static String changeOwnerContactTxt(BotUser user) {
        return user.getSelected_language().equals("uz") ? "Eski tel : "
                + user.getPhoneNumber() + "\n" + SEND_YOUR_CONTACT_UZ
                : user.getSelected_language().equals("ru") ? "Старый тел : "
                + user.getPhoneNumber() + "\n" + SEND_YOUR_CONTACT_RU
                : "Old phone : " + user.getPhoneNumber() + "\n" + SEND_YOUR_CONTACT_ENG;
    }


    static String debtRepoTxt(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qarz :                    "
                : lang.equals("ru") ? "Долг:                    " : "Debt : ";
    }

    static String paidDebtRepoTxt(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "To'langan qarz : "
                : lang.equals("ru") ? "Оплаченныe долги: " : "Paid : ";
    }


    static String addStoreBtnText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? ADD_STORE_UZ
                : lang.equals("ru") ? ADD_STORE_RU
                : ADD_STORE_ENG;
    }

    static String helpText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? HELP_UZ : lang.equals("ru") ? HELP_RU : HELP_ENG;
    }

    static String settingsFromUSer(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? SETTINGS_UZ : lang.equals("ru") ? SETTINGS_RU : SETTINGS_ENG;
    }


    static String selectMenu(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? CHOOSE_MENU_UZ
                : lang.equals("ru") ? CHOOSE_MENU_RU
                : CHOOSE_MENU_ENG;
    }


    static String storesBtnText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? SEE_STORES_UZ
                : lang.equals("ru") ? SEE_STORES_RU
                : SEE_STORES_ENG;
    }

    static String seeCustomersBtnText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? CUSTOMERS_UZ : lang.equals("ru") ? CUSTOMERS_RU : CUSTOMERS_ENG;
    }

    static String enterYourFullNameRequestText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "To'liq ismingizni kiriting \uD83D\uDCDD"
                : lang.equals("ru") ? "Введите свое полное имя \uD83D\uDCDD"
                : "Enter your full name \uD83D\uDCDD";
    }

    static String enterYourRealName(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Iltimos ismingizni to'g'ri kiriting ❗️"
                : lang.equals("ru") ? "Пожалуйста, введите ваше имя правильно ❗️"
                : "Please enter your name correctly ❗️";
    }

    static String askStoreName(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Do'koningizning nomini kiriting! \uD83D\uDCDD"
                : lang.equals("ru") ? "Введите название вашего магазина! \uD83D\uDCDD"
                : "Enter the name of your store! \uD83D\uDCDD";
    }

    static String sendLocationOfStore(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Do'koningizning manzilini pastdagi tugma orqali yuboring!"
                : lang.equals("ru") ? "Отправьте адрес своего магазина с помощью кнопки ниже!" :
                "Send your store address via the button below!";
    }


    static String sendMyLocationBtnText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Do'konning manzilini jo'natish \uD83D\uDCCD"
                : lang.equals("ru") ? "Отправьте адрес магазина \uD83D\uDCCD" :
                "Send the address of the store \uD83D\uDCCD";
    }

    static String successfullyRegisteredMsg(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Tabriklaymiz siz muvaffaqiyatli ro'yxatdan o'tdingiz.\n" +
                "Botni ishlatish bo'yicha video yo'riqnoma"
                : lang.equals("ru") ? "Поздравляем, вы успешно зарегистрировались.\n" +
                "Видео инструкция как пользоваться ботом"
                : "Congratulations, you have successfully registered.\n" +
                "Video instructions on how to use the bot";
    }


    static String useBotInstruction(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qo'llanma \uD83D\uDCD1"
                : lang.equals("ru") ? "Инструкция \uD83D\uDCD1"
                : "Instruction \uD83D\uDCD1";
    }

    static String useBotInstructionText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Botdan foydalanish uchun yo'riqnoma \uD83D\uDCD1"
                : lang.equals("ru") ? "Инструкция по использованию бота \uD83D\uDCD1"
                : "Instructions for using the bot \uD83D\uDCD1";
    }

    static String sorryStoreNameDoesNotExist(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Kechirasiz, bu doʻkon nomi uchun mos nom emas.\n" +
                " Doʻkon nomi 100 belgidan oshmasligi kerak"
                : lang.equals("ru") ? "Извините, это неправильное название для магазина. \n" +
                "Название магазина не должно превышать 100 символов."
                : "Sorry, this isn't a proper name for a store name.\n" +
                "The name of the shop must not exceed 100 characters";
    }


    static String startWorkingBtnText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Ishni boshlash \uD83D\uDC68\u200D\uD83D\uDCBB"
                : lang.equals("ru") ? "Начать работу\uD83D\uDC68\u200D\uD83D\uDCBB"
                : "Getting Started \uD83D\uDC68\u200D\uD83D\uDCBB";
    }

    // statics

    static String reportText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Hisobot :  " : lang.equals("ru") ? "Отчёт :  " : "Report :  ";
    }

    static String reportTodayText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "<b>Bugun :</b>  " : lang.equals("ru") ? "<b>Сегодня :</b>  " : "<b>Today :</b>  ";
    }

    static String reportYesterdayText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "<b>Kecha :</b>  "
                : lang.equals("ru") ? "<b>Вчерашний день :</b>  " : "<b>Yesterday :</b>  ";
    }

    static String reportWeeklyText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "<b>Haftalik :</b>  "
                : lang.equals("ru") ? "<b>Недельный :</b>  " : "Weekly :</b>  ";
    }

    static String reportMonthlyText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "<b>Oylik :</b>  "
                : lang.equals("ru") ? "<b>Месячный :</b>  " : "Monthly :</b>  ";
    }


    static String seeAllDebtorsListText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qarzdorlarning to'liq ro'yxati \uD83D\uDCC3"
                : lang.equals("ru") ? "Общий список должников \uD83D\uDCC3"
                : "A complete list of debtors \uD83D\uDCC3";
    }

    static String addDebtorText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qarzdor \uD83D\uDCB5"
                : lang.equals("ru") ? "Должник \uD83D\uDCB5"
                : "Debtor \uD83D\uDCB5";
    }

    static String getReportText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Hisobot \uD83D\uDCC8"
                : lang.equals("ru") ? "Отчет \uD83D\uDCC8"
                : "Report \uD83D\uDCC8";
    }

    static String goToHomeMenu(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "\uD83C\uDFE0 Bosh sahifa"
                : lang.equals("ru") ? "\uD83C\uDFE0 Домашняя страница"
                : "\uD83C\uDFE0 Home page";
    }


    static String addDebtorNameText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qarzdorning ismini kiriting \uD83D\uDCDD"
                : lang.equals("ru") ? "Введите имя должника \uD83D\uDCDD"
                : "Enter the name of the debtor \uD83D\uDCDD";
    }

    static String cancelBtn(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Bekor qilish \uD83D\uDEAB"
                : lang.equals("ru") ? "Отмена \uD83D\uDEAB"
                : "Cancel \uD83D\uDEAB";
    }

    static String tryAgainButtonText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qaytadan urinish \uD83D\uDD04"
                : lang.equals("ru") ? "Попробуйте ещё раз \uD83D\uDD04"
                : "Try again \uD83D\uDD04";
    }

    static String confirmText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Tasdiqlash ✅" : lang.equals("ru") ? "Подтверждение ✅" : "Confirmation ✅";
    }

    static String ediText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "\n\n<b><i>avvalgi edi =></i></b>\n\n"
                : lang.equals("ru") ? "\n\n<b><i>предыдущий долг </i></b> =>\n\n"
                : "previous debt : ";
    }

    static String happenText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "\n\n<b><i>keyin bo'ldi =></i></b>\n\n"
                : lang.equals("ru") ? "\n\n<b><i>новый долг </i></b> =>\n\n" : "there was a new loan: ";
    }

    static String errorDebtorName(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Iltimos qarzdorning ismini qaytadan kiriting!\n" +
                "100 ta dan oshmasligi kerak"
                : lang.equals("ru") ? "Пожалуйста, введите имя должника еще раз!\n" +
                "  Не более 100"
                : "Please enter the debtor's name again!\n" +
                "No more than 100";
    }

    static String askDebtorPhoneFromAdmin(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qarzdorning telefon raqamini kiriting \uD83D\uDCF2\n" +
                "Eslatma! Telefon nomer +998######### shaklda kiritilishi shart!"
                : lang.equals("ru") ? "Введите номер телефона должника \uD83D\uDCF2\n" +
                "Примечание! Номер телефона необходимо вводить в виде +998#########!"
                : "Enter the debtor's phone number \uD83D\uDCF2\n" +
                "Note! Phone number must be entered in the form +998#########!";
    }

    static String errorReqMsgPhoneNum(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Iltimos telefon raqamini to'g'ri kiriting!" :
                lang.equals("ru") ? "Пожалуйста, введите номер телефона правильно!"
                        : "Please enter the phone number correctly!";
    }


    static String addDebtToDebtor(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qarz qo'shish ➕"
                : lang.equals("ru") ? "Добавить долг ➕"
                : "Add debt ➕";
    }

    static String reduceDebtFromDebtor(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qarzni to'lash ➖"
                : lang.equals("ru") ? "Оплатить долг  ➖" : "Debt repayment ➖";
    }


    static String howMuchLoanGetFirstTime(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qancha qarz kiritmoqchisiz?"
                : lang.equals("ru") ? "Сколько вы хотите занять?"
                : "How much do you want to borrow?";
    }

    static String howMuchDebtPaid(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qancha qarzni yopmoqchisiz?"
                : lang.equals("ru") ? "Какую сумму долга вы хотите погасить?"
                : "How much debt do you want to pay off?";
    }

    static String enterTrueNumber(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Iltimos xabaringizda harf va belgilardan foydalanmang!"
                : lang.equals("ru") ? "Пожалуйста, не используйте буквы и символы в сообщении!"
                : "Please do not use letters or symbols in your message!";
    }

    static String numberVeryBig(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "O' devona! Bill Gatesda ham buncha pul yo'qku"
                : lang.equals("ru") ? "О сумасшедший! Даже у Билла Гейтса нет столько денег"
                : "Oh crazy! Even Bill Gates doesn't have that much money";
    }


    static String excelText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Excelga chiqarish \uD83D\uDCC4"
                : lang.equals("ru") ? "Экспорт в Excel \uD83D\uDCC4" : "Export to Excel \uD83D\uDCC4";
    }

    static String changeLangTxt(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "O'zingizga kerakli tilni tanlang"
                : lang.equals("ru") ? "Выберите  язык" : "Choose the language you want";
    }

    static String changeLang(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Tilni o'zgartirish"
                : lang.equals("ru") ? "Изменить язык" : "Change the language";
    }


    static String settingsMenuTitleTxt(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Nimani o'gartirishni xoxlaysiz"
                : lang.equals("ru") ? "Что вы хотите изменить?"
                : "What do you want to convert?";
    }


    static String loanBigThanDebt(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "To'lanayotgan pul qarzdan kichik bo'lishi kerak"
                : lang.equals("ru") ? "Выплачиваемая сумма должна быть меньше суммы долга."
                : "The amount being paid must be less than the debt";
    }

    static String debtorText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qarzdorlar" : lang.equals("ru") ? "Должники" : "Debtors";
    }

    static String nameText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "FIO " : lang.equals("ru") ? "ФИО " : "Full name ";
    }


    static String myDebtText(DebtUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qarzingizni ko'rish uchun pastdagi tugmani bosing!"
                : lang.equals("ru") ? "Нажмите кнопку ниже, чтобы просмотреть свой долг!"
                : "Click the button below to view your credit!";
    }

    static String seeDebtBntText(DebtUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qarzimni ko'rish"
                : lang.equals("ru") ? "Посмотреть мой долг" : "See my debt";
    }

    static String debtsText(DebtUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Do'konlardan qarzlarim"
                : lang.equals("ru") ? "Мои долги из магазинов" : "My debts from shops";
    }

    static String storeNameText(DebtUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Do'kon nomi: "
                : lang.equals("ru") ? "Название магазина:" : "Store Name:";
    }

    static String reportStoreNameText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Do'kon nomi: "
                : lang.equals("ru") ? "Название магазина:" : "Store Name:";
    }

    static String numberTxt(DebtUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "☎️: " : lang.equals("ru") ? "☎️:" : "☎️: ";
    }

    static String numberText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "☎️: " : lang.equals("ru") ? "☎️:" : "☎️: ";
    }


    static String noDebtText(DebtUser debtor) {
        String lang = debtor.getSelected_language();
        return lang.equals("uz") ? "Qarzingiz yoq"
                : lang.equals("ru") ? "У вас нету долга" : "You have no debt";
    }

    static String storeLocationText(DebtUser debtor) {
        String lang = debtor.getSelected_language();
        return lang.equals("uz") ? "\uD83D\uDCCD Do'konning manzili"
                : lang.equals("ru") ? "\uD83D\uDCCD Адрес магазина" : "\uD83D\uDCCD Address of the store";
    }

    static String backMenuText(DebtUser debtor) {
        String lang = debtor.getSelected_language();
        return lang.equals("uz") ? "⬅️ orqaga"
                : lang.equals("ru") ? "⬅️ назад" : "⬅️ back";
    }


    static String searchDebtorsText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Mijozni topish uchun raqamini yoki ism familyasini kiriting \uD83D\uDD0E"
                : lang.equals("ru") ? "Для поиска клиента введите его номер или имя-фамилию \uD83D\uDD0E"
                : "To find a customer, enter his number or name \uD83D\uDD0E";
    }

    static String searchText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qarzdorni qidirish \uD83D\uDD0E"
                : lang.equals("ru") ? "Поиск должника \uD83D\uDD0E"
                : "Search debtor \uD83D\uDD0E";
    }

    static String notFoundUser(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Bunday qarzdor topilmadi (("
                : lang.equals("ru") ? "Такого должника не нашлось(("
                : "No such debtor found ((";
    }


    static String changeStoreInfo(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Do'kon malumotlari"
                : lang.equals("ru") ? "Информация о магазине" : "Store information";
    }

    static String changeDebtorInfo(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qarzdor malumotlari"
                : lang.equals("ru") ? "Информация о должнике" : "Debtor information";
    }

    static String changeStoreName(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Do'kon nomi"
                : lang.equals("ru") ? "Название магазина"
                : "Store name";
    }

    static String backTxt(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "⬅️orqaga"
                : lang.equals("ru") ? "⬅️ назад"
                : "⬅️ back";
    }

    static String whichDYLChangeDebtor(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qaysi qarzdorni maluotini o'zgartirmoqchisiz"
                : lang.equals("ru") ? "Статус какого должника вы хотите изменить?"
                : "Which debtor do you want to change the status of?";
    }

    static String newStoreName(BotUser user, String storeName) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Do'konning eski nomi: " + storeName + "\nDo'koningizning yangi nomini kiriting"
                : lang.equals("ru") ? "Старое название магазина: " + storeName + "\nВведите новое название для вашего магазина"
                : "Old Store Name: " + storeName + "\nEnter a new name for your store";
    }

    static String changeStoreLocation(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Do'kon manzili"
                : lang.equals("ru") ? "Адрес магазина"
                : "Store address";
    }

    static String changeStoreInformation(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Do'konning qaysi malumotini o'zgartirmoqchisiz"
                : lang.equals("ru") ? "Какую информацию о магазине вы хотите изменить?"
                : "Which store information do you want to change?";
    }

    static String changeDebtorInformation(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qarzdorning qaysi malumotini o'zgartirmoqchisiz"
                : lang.equals("ru") ? "Какую информацию о должнике вы хотите изменить?"
                : "Which debtor information do you want to change?";
    }

    static String ownCabinetTxt(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Shaxsiy kabinet"
                : lang.equals("ru") ? "Личный кабинет"
                : "Personal cabinet";
    }

    static String cabinetMenuTxt(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qaysi malumotingizni o'zgartirmoqchisiz?"
                : lang.equals("ru") ? "Какую информацию вы хотите изменить?"
                : "What information do you want to change?";
    }


    static String ownerNameTxt(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "FIO : " + user.getFullName()
                : lang.equals("ru") ? "ФИО : " + user.getFullName()
                : "FullName : " + user.getFullName();
    }

    static String ownerPhoneTxt(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Tel : " + user.getPhoneNumber()
                : lang.equals("ru") ? "Тел : " + user.getPhoneNumber()
                : "Phone : " + user.getPhoneNumber();
    }

    static String changingDebtorAsk(BotUser user, Debtor debtor) {
        String lang = user.getSelected_language();

        return lang.equals("uz") ? "Qarzdor : " + debtor.getDebtorFullName() + "\n" +
                "Tel : " + debtor.getPhoneNumber() + "\n" + changeDebtorInformation(user)
                : lang.equals("ru") ? "должник : " + debtor.getDebtorFullName() + "\n" +
                "Тел : " + debtor.getPhoneNumber() + "\n" + changeDebtorInformation(user)
                : "Debtor : " + debtor.getDebtorFullName() + "\n" + changeDebtorInformation(user);
    }

    static String changingDebtorAsking(BotUser user, Debtor debtor) {
        String lang = user.getSelected_language();

        return lang.equals("uz") ? "Qarzdor : " + debtor.getDebtorFullName() + "\n" +
                "Tel : " + debtor.getPhoneNumber() + "\n"
                : lang.equals("ru") ? "должник : " + debtor.getDebtorFullName() + "\n" +
                "Тел : " + debtor.getPhoneNumber() + "\n"
                : "Debtor : " + debtor.getDebtorFullName() + "\n";
    }

    static String debtorNameTxt(BotUser user, Debtor debtor) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "FIO : " + debtor.getDebtorFullName()
                : lang.equals("ru") ? "ФИО : " + debtor.getDebtorFullName()
                : "FullName : " + debtor.getDebtorFullName();
    }

    static String debtorPhoneTxt(BotUser user, Debtor debtor) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "FIO : " + debtor.getPhoneNumber()
                : lang.equals("ru") ? "ФИО : " + debtor.getPhoneNumber()
                : "FullName : " + debtor.getPhoneNumber();
    }

    static String changeDebtorNameTitle(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "\nQardorning yangi ismini kiriting"
                : lang.equals("ru") ? "\nВведите новое имя клиента"
                : "\nEnter the new name of the customer";
    }

    static String wYLAND(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Yangi qarzdor qo'shish uchun bosing"
                : lang.equals("ru") ? "Нажмите, чтобы добавить нового должника"
                : "Click to add a new debtor";
    }

    static String leftOverText(BotUser user) {
        String lang = user.getSelected_language();
        return lang.equals("uz") ? "Qoldi :                    "
                : lang.equals("ru") ? "Осталося :                  "
                : "Left in debt : ";
    }
}

