package br.com.alura.screenmatch.model;

public enum Category {
    ACTION("Action"),
    DRAMA("Drama"),
    ROMANCE("Romance"),
    COMEDY("Comedy"),
    HORROR("Horror"),
    CRIME("Crime"),
    ADVENTURE("Adventure");

    private String omdbCategory;

    Category(String omdbCategory){
        this.omdbCategory = omdbCategory;
    }
    public static Category fromString(String text) {
        for (Category category : Category.values()) {
            if (category.omdbCategory.equalsIgnoreCase(text)) {
                return category;
            }
        }
        throw new IllegalArgumentException("No category found using the String given as parameter: " + text);
    }

}
