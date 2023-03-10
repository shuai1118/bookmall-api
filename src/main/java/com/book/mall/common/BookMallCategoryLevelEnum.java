
package com.book.mall.common;


public enum BookMallCategoryLevelEnum {

    DEFAULT(0, "ERROR"),
    LEVEL_ONE(1, "一级分类"),
    LEVEL_TWO(2, "二级分类"),
    LEVEL_THREE(3, "三级分类");

    private int level;

    private String name;

    BookMallCategoryLevelEnum(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public static BookMallCategoryLevelEnum getBookMallOrderStatusEnumByLevel(int level) {
        for (BookMallCategoryLevelEnum bookMallCategoryLevelEnum : BookMallCategoryLevelEnum.values()) {
            if (bookMallCategoryLevelEnum.getLevel() == level) {
                return bookMallCategoryLevelEnum;
            }
        }
        return DEFAULT;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
