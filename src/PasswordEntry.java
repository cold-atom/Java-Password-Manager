public class PasswordEntry {
    private String title;
    private String username;
    private String password;
    private String url;
    
    public PasswordEntry(String title, String username, String password, String url) {
        this.title = title;
        this.username = username;
        this.password = password;
        this.url = url;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String toCsvRow() {
        String encryptedPassword = VigenereCipher.encrypt(password);
        return String.join(",",
            escapeCsv(title),
            escapeCsv(username),
            escapeCsv(encryptedPassword),
            escapeCsv(url)
        );
    }
    
    private String escapeCsv(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
    
    public static PasswordEntry fromCsvRow(String csvRow) {
        String[] parts = csvRow.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (parts.length < 4) {
            return null;
        }
        String title = unescapeCsv(parts[0]);
        String username = unescapeCsv(parts[1]);
        String encryptedPassword = unescapeCsv(parts[2]);
        String password = VigenereCipher.decrypt(encryptedPassword);
        String url = unescapeCsv(parts[3]);
        return new PasswordEntry(title, username, password, url);
    }
    
    private static String unescapeCsv(String field) {
        if (field.startsWith("\"") && field.endsWith("\"")) {
            field = field.substring(1, field.length() - 1);
            field = field.replace("\"\"", "\"");
        }
        return field;
    }
}
