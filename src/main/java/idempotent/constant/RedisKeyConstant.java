package idempotent.constant;

/**
 * @Description
 * @Author LIJIAN
 * @Date 2023/9/9 13:51
 */
public class RedisKeyConstant {
    private static final String USER_PACK_USER_ID_LIST_KEY = "userpack:findUserIds: ";
    private static final String REDEEM_CODE_DISTRUBUTE_LOCK_KEY = "redeemcode_distrubute:lock";
    private static final String COUPON_DISTRUBUTE_LOCK_KEY = "coupon_distrubute:lock";
    public static final String PREVENT_DUPLICATION_PREFIX = "prevent_duplication_prefix:";

    public static String getUserPackUserIdListKey(Long storeId) {
        return USER_PACK_USER_ID_LIST_KEY + storeId.toString();
    }

    public static String generateRedeemCodeDistrubuteLockKey(Long redeemcodeId){
        return REDEEM_CODE_DISTRUBUTE_LOCK_KEY
                .concat(":")
                .concat(redeemcodeId.toString());
    }

    public static String generateCouponDistrubuteLockKey(Long couponId){
        return COUPON_DISTRUBUTE_LOCK_KEY
                .concat(":")
                .concat(couponId.toString());
    }
}
