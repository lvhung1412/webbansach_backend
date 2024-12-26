package vn.lvhung.webbansach_backend.security;

public class Endpoints {
    public static final String front_end_host = "http://localhost:3000";
    public static final String[] PUBLIC_GET_ENDPOINTS = {
            "/books",
            "/books/**",
            "/genre/**",
            "/images/**",
            "/reviews/**",
            "/users/search/existsByUsername/**",
            "/users/search/existsByEmail/**",
            "/user/active-account/**",
            "/cart-items/**",
            "/users/*/listCartItems",
            "/orders/**",
            "/order-detail/**",
            "/users/*/listOrders",
            "/users/*/listRoles",
            "/users/*",
            "/favorite-book/get-favorite-book/**",
            "/users/*/listFavoriteBooks",
            "/favorite-books/*/book",
            "/vnpay/**",
    };

    public static final String[] PUBLIC_POST_ENDPOINTS = {
            "/user/register",
            "/user/authenticate",
            "/cart-item/add-item",
            "/order/**",
            "/review/add-review/**",
            "/feedback/add-feedback",
            "/favorite-book/add-book",
            "/vnpay/create-payment/**",
            "/review/get-review/**",
    };

    public static final String[] PUBLIC_PUT_ENDPOINTS = {
            "/cart-item/**",
            "/cart-items/**",
            "/users/**",
            "/user/update-profile",
            "/user/change-password",
            "/user/forgot-password",
            "/user/change-avatar",
            "/order/update-order",
            "/order/cancel-order",
            "/review/update-review"
    };

    public static final String[] PUBLIC_DELETE_ENDPOINTS = {
            "/cart-items/**",
            "/favorite-book/delete-book",
    };

    public static final String[] ADMIN_ENDPOINT_ENDPOINTS = {
            "/users",
            "/users/**",
            "/cart-items/**",
            "/books",
            "/books/**",
            "/book/add-book/**",
            "/user/add-user/**",
            "/feedbacks/**",
            "/cart-items/**",
            "/cart-item/**",
            "/orders/**",
            "/order/**",
            "/order-detail/**",
            "/roles/**",
            "/favorite-book/**",
            "/favorite-books/**",
            "/review/**",
            "/book/get-total/**",
            "/feedbacks/search/countBy/**",
            "/**",

    };
}
