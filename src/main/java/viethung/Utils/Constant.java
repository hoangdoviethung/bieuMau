package viethung.Utils;

/**
 * @author itsol.hungtt
 */
public interface Constant {


    public class ColumnExcelError{
        public static final String excelString =" String rỗng hoặc empty";
        public static final String excelNumber ="Số không đúng định dạng";
        public static final String excelDatetime ="Thời gian không đúng định dạng";
        public static final String excelPhoneNumber ="Số điên thoại không đúng định dạng";

    }

    interface ApiName {
        String API_CARD_OPCDE = "API_CARD_OPCDE";

        String API = "API";

        String API_KEY = "API_KEY";

        String GET_TRANSACTION = "GET_TRANSACTION";
        String GET_ENQUIRY = "GET_ENQUIRY";
    }

    interface ActionCard {
        String CLOSE_CARD = "CLOSE_CARD"; //đóng thẻ
        String UPDATE_STATUS = "UPDATE_STATUS"; //thay đổi trạng thái thẻ
        String UPDATE_TRANS_LIMIT = "UPDATE_TRANS_LIMIT"; // thay đổi hạn mức giao dich
        String UPDATE_INFO = "UPDATE_INFO"; //thay đổi thông tin thẻ
        String RESET_PIN_CARD_SERCURE = "RESET_PIN_CARD_SERCURE"; //ReSet pin 3d secure
        String RELEASE_CARD_PIN = "RELEASE_CARD_PIN"; //phát hành lại thẻ pin
        String EXTEND_CARD = "EXTEND_CARD"; //Gia hạn thẻ
        String RELEASE_SUB_CARD = "RELEASE_SUB_CARD"; //phát hàng thẻ phụ
        String MANUAL_CLOSE_CARD = "MANUAL_CLOSE_CARD"; //đóng thẻ thủ công
    }

    interface BusinessType {
        String BUSINESS_TYPE_EXTEND = "EXTEND_CARD";
        String BUSINESS_TYPE_RELEASE = "RELEASE_CARD";
        String BUSINESS_TYPE_CLOSE = "CLOSE_CARD";
    }

    interface CardStatus {
        String CARD_OK = "14";
        String PIN_BLOCKED = "185";
        String CARD_DO_NOT_HONOR = "98";
        String PICK_UP_L41 = "74";
        String CARD_CLOSE = "109";

        String PROCESSING = "PROCESSING";
    }

    interface FeeMethod {
        String FEE_TIEN_MAT = "TIEN_MAT";
        String FEE_TAI_KHOAN = "TAI_KHOAN";
        String FEE_MIEN_PHI = "MIEN_PHI";
    }

    interface TypeLimitCard {
        String ATM_NOI_DIA = "trnx";
        String ATM_QUOC_TE = "cw";
        String POS = "pos_r";
        String ECOM = "ecom_r";
    }

    /**
     * Phuc vu xuat file excel
     */
    public class ColumnType{
        public static final String SINGLE_COLUMN ="Single";
        public static final String LISTED_COLUMN ="List";

    }
}
