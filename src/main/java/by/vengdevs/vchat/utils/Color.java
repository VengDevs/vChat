package by.vengdevs.vchat.utils;

public class Color {

    public static String convert(String specialSymbol, String text) {
        return text.replaceAll(specialSymbol, "§");
    }
}
