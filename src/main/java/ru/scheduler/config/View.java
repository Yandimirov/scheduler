package ru.scheduler.config;

public class View {
    public static class BASE{};
    public static class MESSAGE extends BASE{};
    public static class SUMMARY extends MESSAGE{};
    public static class AUTH extends BASE{};
    public static class EVENT extends MESSAGE{};
}
