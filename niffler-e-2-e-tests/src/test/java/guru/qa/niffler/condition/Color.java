package guru.qa.niffler.condition;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Color {
    yellow("rgba(255, 183, 3, 1)"),
    green("rgba(53, 173, 123, 1)"),
    red("rgba(247, 89, 67, 1)"),
    blue100("rgba(41, 65, 204, 1)"),
    orange("rgba(251, 133, 0, 1)");

    public final String rgb;

    public static Color getByValue(String rgba) {
        for (Color color : values()) {
            if (color.rgb.equals(rgba)) {
                return color;
            }
        }
        throw new IllegalArgumentException("No enum for color: " + rgba);
    }
}