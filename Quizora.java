import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Quizora extends JFrame {

    private static final Color BG = new Color(247, 245, 255);
    private static final Color PURPLE = new Color(105, 72, 205);
    private static final Color PURPLE_DARK = new Color(74, 48, 151);
    private static final Color DARK = new Color(42, 34, 72);
    private static final Color MUTED = new Color(112, 104, 137);
    private static final Color WHITE = Color.WHITE;
    private static final Color BORDER = new Color(225, 218, 247);
    private static final Color GREEN_BG = new Color(220, 248, 230);
    private static final Color GREEN = new Color(29, 126, 69);
    private static final Color RED_BG = new Color(255, 229, 232);
    private static final Color RED = new Color(175, 52, 67);

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel root = new JPanel(cardLayout);
    private final Map<String, List<Question>> questionBank =
            new LinkedHashMap<>();

    private final File historyFile = new File("quizora_history.txt");

    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;

    private String studentName = "";
    private String studentPhone = "";
    private String studentEmail = "";

    private String selectedSubject = "";
    private String selectedLevel = "";

    private JLabel mascotLabel;
    private JLabel greetingLabel;
    private JLabel dashboardScoreLabel;
    private JLabel dashboardHighScoreLabel;

    private final List<Question> activeQuestions = new ArrayList<>();

    private int questionIndex = 0;
    private int score = 0;
    private int secondsRemaining = 20;
    private int selectedAnswer = -1;

    private javax.swing.Timer quizTimer;
    private javax.swing.Timer mascotTimer;

    private JLabel quizHeadingLabel;
    private JLabel questionLabel;
    private JLabel questionCountLabel;
    private JLabel timerLabel;
    private JLabel feedbackLabel;

    private JProgressBar progressBar;
    private JButton[] optionButtons;
    private JButton nextButton;

    private JLabel resultTitleLabel;
    private JLabel resultStudentLabel;
    private JLabel resultScoreLabel;
    private JLabel resultPercentLabel;
    private JLabel resultMessageLabel;
    private JLabel resultHighScoreLabel;

    // QUESTION MODEL

    private static class Question {

        final String text;
        final String[] options;
        final int correctIndex;

        Question(String text, String a, String b,
                 String c, String d, int correctIndex) {

            this.text = text;
            this.options = new String[]{a, b, c, d};
            this.correctIndex = correctIndex;
        }
    }

    // APPLICATION START

    public Quizora() {

        super("QUIZORA | Interactive Quiz & Assessment System");

        loadQuestions();

        root.setBackground(BG);

        root.add(createWelcomeScreen(), "welcome");
        root.add(createDashboardScreen(), "dashboard");
        root.add(createQuizScreen(), "quiz");
        root.add(createResultScreen(), "result");

        setContentPane(root);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 680));
        setSize(1050, 780);
        setLocationRelativeTo(null);
        setVisible(true);

        startMascotAnimation();
    }

    // QUESTION BANK HELPERS

    private void addQuestion(String subject, String level,
                             String text, String a, String b,
                             String c, String d, int answer) {

        String key = makeKey(subject, level);

        questionBank.computeIfAbsent(
                key, k -> new ArrayList<>()
        ).add(new Question(text, a, b, c, d, answer));
    }

    private String makeKey(String subject, String level) {
        return subject + "|" + level;
    }

    // ALL SUBJECT QUESTIONS

    private void loadQuestions() {

        // MATHEMATICS - EASY

        addQuestion("Mathematics", "Easy",
                "What is 12 + 8?", "18", "20", "22", "24", 1);

        addQuestion("Mathematics", "Easy",
                "What is 7 x 6?", "36", "40", "42", "48", 2);

        addQuestion("Mathematics", "Easy",
                "What is half of 50?", "20", "25", "30", "15", 1);

        // MATHEMATICS - INTERMEDIATE

        addQuestion("Mathematics", "Intermediate",
                "What is 15% of 200?", "20", "25", "30", "35", 2);

        addQuestion("Mathematics", "Intermediate",
                "Solve: 3x = 27", "6", "7", "8", "9", 3);

        addQuestion("Mathematics", "Intermediate",
                "What is the square of 13?", "156", "169", "179", "196", 1);

        // MATHEMATICS - HARD

        addQuestion("Mathematics", "Hard",
                "What is the value of 2 to the power of 5?",
                "16", "24", "32", "64", 2);

        addQuestion("Mathematics", "Hard",
                "What is the square root of 625?",
                "15", "20", "25", "35", 2);

        addQuestion("Mathematics", "Hard",
                "If x + 7 = 19, what is x?",
                "10", "11", "12", "13", 2);


        // ENGLISH - EASY

        addQuestion("English", "Easy",
                "Choose the plural of child.",
                "Childs", "Children", "Childes", "Childrens", 1);

        addQuestion("English", "Easy",
                "Which word is a noun?",
                "Quickly", "Beautiful", "Teacher", "Run", 2);

        addQuestion("English", "Easy",
                "Choose the opposite of hot.",
                "Warm", "Cold", "Heat", "Boil", 1);

        // ENGLISH - INTERMEDIATE

        addQuestion("English", "Intermediate",
                "Choose the correct sentence.",
                "She go to school.",
                "She goes to school.",
                "She going school.",
                "She gone school.", 1);

        addQuestion("English", "Intermediate",
                "What is the past tense of write?",
                "Writed", "Written", "Wrote", "Writing", 2);

        addQuestion("English", "Intermediate",
                "Choose a synonym for happy.",
                "Joyful", "Angry", "Tired", "Afraid", 0);

        // ENGLISH - HARD

        addQuestion("English", "Hard",
                "Identify the adjective in: The sky is blue.",
                "Sky", "Is", "The", "Blue", 3);

        addQuestion("English", "Hard",
                "Choose the correct passive voice.",
                "The cake ate him.",
                "The cake was eaten by him.",
                "The cake is eat him.",
                "The cake eating him.", 1);

        addQuestion("English", "Hard",
                "Which word is spelled correctly?",
                "Accomodation", "Acommodation",
                "Accommodation", "Acomodation", 2);


        // SCIENCE - EASY

        addQuestion("Science", "Easy",
                "Which planet is known as the Red Planet?",
                "Earth", "Mars", "Venus", "Jupiter", 1);

        addQuestion("Science", "Easy",
                "What gas do humans need to breathe?",
                "Oxygen", "Helium", "Hydrogen", "Neon", 0);

        addQuestion("Science", "Easy",
                "How many legs does an insect have?",
                "Four", "Six", "Eight", "Ten", 1);

        // SCIENCE - INTERMEDIATE

        addQuestion("Science", "Intermediate",
                "What is the chemical formula of water?",
                "CO2", "O2", "H2O", "NaCl", 2);

        addQuestion("Science", "Intermediate",
                "Which organ pumps blood around the body?",
                "Lungs", "Brain", "Kidney", "Heart", 3);

        addQuestion("Science", "Intermediate",
                "What force pulls objects towards Earth?",
                "Friction", "Gravity", "Magnetism", "Pressure", 1);

        // SCIENCE - HARD

        addQuestion("Science", "Hard",
                "What is the SI unit of force?",
                "Joule", "Watt", "Newton", "Pascal", 2);

        addQuestion("Science", "Hard",
                "Which cell organelle produces most ATP?",
                "Nucleus", "Mitochondrion", "Ribosome", "Golgi body", 1);

        addQuestion("Science", "Hard",
                "What is the approximate speed of light in vacuum?",
                "300,000 km/s", "30,000 km/s",
                "3,000 km/s", "3,000,000 km/s", 0);


        // SOCIAL STUDIES - EASY

        addQuestion("Social Studies", "Easy",
                "What is the capital of India?",
                "Mumbai", "New Delhi", "Chennai", "Kolkata", 1);

        addQuestion("Social Studies", "Easy",
                "How many days are there in a week?",
                "5", "6", "7", "8", 2);

        addQuestion("Social Studies", "Easy",
                "Which is the largest ocean?",
                "Indian", "Arctic", "Atlantic", "Pacific", 3);

        // SOCIAL STUDIES - INTERMEDIATE

        addQuestion("Social Studies", "Intermediate",
                "In which year did India gain independence?",
                "1945", "1947", "1950", "1952", 1);

        addQuestion("Social Studies", "Intermediate",
                "Which river is traditionally regarded as India's longest?",
                "Yamuna", "Godavari", "Ganga", "Kaveri", 2);

        addQuestion("Social Studies", "Intermediate",
                "What is the supreme law of a country called?",
                "Manifesto", "Constitution", "Budget", "Treaty", 1);

        // SOCIAL STUDIES - HARD

        addQuestion("Social Studies", "Hard",
                "Who chaired the Drafting Committee of India's Constitution?",
                "Jawaharlal Nehru", "Sardar Patel",
                "B. R. Ambedkar", "Rajendra Prasad", 2);

        addQuestion("Social Studies", "Hard",
                "Which imaginary line is at 0 degrees latitude?",
                "Tropic of Cancer", "Prime Meridian",
                "Equator", "Arctic Circle", 2);

        addQuestion("Social Studies", "Hard",
                "Which ancient civilization developed along the Indus River?",
                "Roman", "Indus Valley", "Mayan", "Greek", 1);


        // GENERAL KNOWLEDGE - EASY

        addQuestion("General Knowledge", "Easy",
                "How many colours are traditionally listed in a rainbow?",
                "Five", "Six", "Seven", "Eight", 2);

        addQuestion("General Knowledge", "Easy",
                "Which animal is commonly called the King of the Jungle?",
                "Tiger", "Lion", "Elephant", "Bear", 1);

        addQuestion("General Knowledge", "Easy",
                "Which is the national bird of India?",
                "Parrot", "Peacock", "Eagle", "Swan", 1);

        // GENERAL KNOWLEDGE - INTERMEDIATE

        addQuestion("General Knowledge", "Intermediate",
                "Which is the largest continent?",
                "Africa", "Europe", "Asia", "Australia", 2);

        addQuestion("General Knowledge", "Intermediate",
                "How many players from one cricket team are on the field during normal play?",
                "9", "10", "11", "12", 2);

        addQuestion("General Knowledge", "Intermediate",
                "Which instrument measures temperature?",
                "Barometer", "Thermometer", "Compass", "Hygrometer", 1);

        // GENERAL KNOWLEDGE - HARD

        addQuestion("General Knowledge", "Hard",
                "What is the currency of Japan?",
                "Won", "Yuan", "Yen", "Ringgit", 2);

        addQuestion("General Knowledge", "Hard",
                "What is the smallest prime number?",
                "0", "1", "2", "3", 2);

        addQuestion("General Knowledge", "Hard",
                "Which is the largest hot desert in the world?",
                "Gobi", "Sahara", "Kalahari", "Arabian", 1);
    }

    // COMMON UI HELPERS

    private JPanel page() {
        JPanel p = new JPanel(new BorderLayout(18, 18));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(22, 28, 18, 28));
        return p;
    }

    private JLabel label(String text, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, size));
        l.setForeground(color);
        return l;
    }

    private JLabel heading(String text, int size) {
        JLabel l = label(text, size, DARK);
        l.setFont(new Font("SansSerif", Font.BOLD, size));
        return l;
    }

    private JButton makeButton(String text, Color background) {
        JButton b = new JButton(text);

        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setForeground(WHITE);
        b.setBackground(background);

        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);

        b.setBorder(new EmptyBorder(11, 16, 11, 16));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        b.setMinimumSize(new Dimension(120, 42));
        b.setPreferredSize(new Dimension(160, 44));

        return b;
    }

    private JPanel whiteCard() {
        JPanel p = new JPanel();

        p.setBackground(WHITE);
        p.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(15, 17, 15, 17)));

        return p;
    }

    private JTextField textField(String placeholder) {
        JTextField f = new JTextField();

        f.setFont(new Font("SansSerif", Font.PLAIN, 15));
        f.setForeground(DARK);
        f.setBackground(WHITE);
        f.setCaretColor(PURPLE);
        f.setPreferredSize(new Dimension(350, 39));

        f.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(7, 10, 7, 10)));

        f.setToolTipText(placeholder);

        return f;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = label(text, 11, MUTED);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setBorder(new EmptyBorder(0, 2, 5, 0));
        return l;
    }

    private JPanel inputRow(String title, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(4, 5));
        row.setOpaque(false);

        row.add(fieldLabel(title), BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);

        row.setMaximumSize(new Dimension(380, 62));
        row.setPreferredSize(new Dimension(380, 62));

        return row;
    }

    private void startMascotAnimation() {
        final String[] frames = {
                "🦉", "🦉✨", "🦉📚", "🦉💜"
        };

        mascotTimer = new javax.swing.Timer(650, e -> {
            if (mascotLabel != null) {
                int next = (int) ((System.currentTimeMillis() / 650)
                        % frames.length);
                mascotLabel.setText(frames[next]);
            }
        });

        mascotTimer.start();
    }

    // WELCOME SCREEN

    private JPanel createWelcomeScreen() {
        JPanel p = page();

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel brand = label("QUIZORA  ✦", 23, PURPLE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 23));

        header.add(brand, BorderLayout.WEST);
        header.add(label("INTERACTIVE QUIZ & ASSESSMENT",
                11, MUTED), BorderLayout.EAST);

        p.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel form = whiteCard();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(450, 530));

        form.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(20, 28, 20, 28)));

        mascotLabel = label("🦉", 48, PURPLE);
        mascotLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mascotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = heading("Welcome to Quizora!", 27);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = label(
                "Learn something new every day.", 14, MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = textField("Enter your full name");
        phoneField = textField("Enter your 10-digit phone number");
        emailField = textField("Enter your email address");

        form.add(mascotLabel);
        form.add(Box.createVerticalStrut(4));
        form.add(title);
        form.add(Box.createVerticalStrut(6));
        form.add(subtitle);
        form.add(Box.createVerticalStrut(20));

        form.add(inputRow("YOUR NAME", nameField));
        form.add(Box.createVerticalStrut(10));

        form.add(inputRow("PHONE NUMBER", phoneField));
        form.add(Box.createVerticalStrut(10));

        form.add(inputRow("EMAIL ADDRESS", emailField));
        form.add(Box.createVerticalStrut(18));

        JButton start = makeButton("Let's Start Learning  →", PURPLE);
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.setMaximumSize(new Dimension(380, 46));
        start.addActionListener(e -> startApplication());

        form.add(start);

        center.add(form);
        p.add(center, BorderLayout.CENTER);

        JLabel footer = label(
                "Practice  •  Progress  •  Achieve", 12, MUTED);
        footer.setHorizontalAlignment(SwingConstants.CENTER);

        p.add(footer, BorderLayout.SOUTH);

        return p;
    }

    private void startApplication() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in your name, phone number and email address.",
                    "Missing details",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!phone.matches("[0-9]{10}")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid 10-digit phone number.",
                    "Check phone number",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid email address.",
                    "Check email address",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        studentName = name;
        studentPhone = phone;
        studentEmail = email;

        greetingLabel.setText(
                "Hello, " + studentName + "!  Welcome back");

        setTitle("QUIZORA | Welcome, " + studentName);

        updateDashboardSummary();

        cardLayout.show(root, "dashboard");
    }

    // DASHBOARD SCREEN

    private JPanel createDashboardScreen() {
        JPanel p = page();

        JPanel header = new JPanel(new BorderLayout(15, 10));
        header.setOpaque(false);

        JPanel brandBlock = new JPanel();
        brandBlock.setOpaque(false);
        brandBlock.setLayout(new BoxLayout(
                brandBlock, BoxLayout.Y_AXIS));

        JLabel brand = label("QUIZORA  ✦", 15, PURPLE);
        brand.setFont(new Font("SansSerif", Font.BOLD, 15));

        brandBlock.add(brand);
        brandBlock.add(Box.createVerticalStrut(4));
        brandBlock.add(heading("Choose your subject", 28));

        header.add(brandBlock, BorderLayout.WEST);

        JButton historyButton = makeButton("My Quiz History", DARK);
        historyButton.setPreferredSize(new Dimension(155, 42));
        historyButton.addActionListener(e -> showHistory());

        header.add(historyButton, BorderLayout.EAST);

        p.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setOpaque(false);

        JPanel greeting = whiteCard();
        greeting.setLayout(new BoxLayout(
                greeting, BoxLayout.Y_AXIS));

        greetingLabel = heading(
                "Hello, Learner!  Welcome back", 19);
        greetingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel greetingSub = label(
                "Pick a subject and start your challenge.",
                13, MUTED);
        greetingSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        greeting.add(greetingLabel);
        greeting.add(Box.createVerticalStrut(5));
        greeting.add(greetingSub);

        content.add(greeting, BorderLayout.NORTH);

        JPanel subjects = new JPanel(new GridLayout(2, 3, 14, 14));
        subjects.setOpaque(false);

        subjects.add(createSubjectCard(
                "Mathematics", "∑",
                "Numbers and problem solving",
                new Color(111, 76, 210)));

        subjects.add(createSubjectCard(
                "English", "Aa",
                "Grammar and vocabulary",
                new Color(50, 124, 197)));

        subjects.add(createSubjectCard(
                "Science", "⚗",
                "Explore the world around you",
                new Color(32, 151, 126)));

        subjects.add(createSubjectCard(
                "Social Studies", "◎",
                "History and geography",
                new Color(213, 132, 51)));

        subjects.add(createSubjectCard(
                "General Knowledge", "✦",
                "Discover something new",
                new Color(190, 83, 142)));

        JPanel emptyCard = whiteCard();
        emptyCard.setLayout(new BorderLayout(5, 5));

        JLabel littleMascot = label("🦉", 39, PURPLE);
        littleMascot.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel littleText = label("Keep learning!", 13, MUTED);
        littleText.setHorizontalAlignment(SwingConstants.CENTER);

        emptyCard.add(littleMascot, BorderLayout.CENTER);
        emptyCard.add(littleText, BorderLayout.SOUTH);

        subjects.add(emptyCard);

        content.add(subjects, BorderLayout.CENTER);

        JPanel summary = whiteCard();
        summary.setLayout(new GridLayout(2, 1, 4, 4));

        dashboardScoreLabel = label(
                "YOUR PROGRESS  •  Complete your first quiz to see your latest score.",
                12, DARK);

        dashboardHighScoreLabel = label(
                "HIGH SCORE  •  Your best result will appear here.",
                12, PURPLE_DARK);

        summary.add(dashboardScoreLabel);
        summary.add(dashboardHighScoreLabel);

        content.add(summary, BorderLayout.SOUTH);

        p.add(content, BorderLayout.CENTER);

        JLabel footer = label(
                "QUIZORA  •  Learn at your own pace", 11, MUTED);

        p.add(footer, BorderLayout.SOUTH);

        return p;
    }

    private JPanel createSubjectCard(
            String subject, String symbol,
            String description, Color accent) {

        JPanel card = whiteCard();
        card.setLayout(new BorderLayout(8, 10));

        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(14, 15, 14, 15)));

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(
                textBlock, BoxLayout.Y_AXIS));

        JLabel icon = label(symbol, 29, accent);
        icon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = label(subject, 16, DARK);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = label(
                "<html><div style='width:210px'>"
                        + escapeHtml(description)
                        + "</div></html>",
                11, MUTED);

        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        textBlock.add(icon);
        textBlock.add(Box.createVerticalStrut(5));
        textBlock.add(title);
        textBlock.add(Box.createVerticalStrut(4));
        textBlock.add(desc);

        card.add(textBlock, BorderLayout.CENTER);

        JButton choose = makeButton("Choose Level  →", accent);
        choose.setPreferredSize(new Dimension(150, 42));

        choose.addActionListener(e -> chooseLevel(subject));

        card.add(choose, BorderLayout.SOUTH);

        return card;
    }

    private void chooseLevel(String subject) {

        String[] levels = {
                "Easy", "Intermediate", "Hard"
        };

        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Choose a difficulty level for " + subject + ":",
                "Select Difficulty",
                JOptionPane.QUESTION_MESSAGE,
                null,
                levels,
                levels[0]);

        if (choice != null) {
            selectedSubject = subject;
            selectedLevel = choice;
            startQuiz();
        }
    }

    private void updateDashboardSummary() {

        if (dashboardScoreLabel == null
                || dashboardHighScoreLabel == null) {
            return;
        }

        String last = getLastHistoryLine();

        if (last == null) {
            dashboardScoreLabel.setText(
                    "YOUR PROGRESS  •  Complete your first quiz to see your latest score.");
        } else {
            dashboardScoreLabel.setText(
                    "YOUR PROGRESS  •  Last attempt: " + last);
        }

        int best = getOverallHighScore();

        dashboardHighScoreLabel.setText(
                "HIGH SCORE  •  Best percentage across your quiz attempts: "
                        + best + "%");
    }

    // QUIZ SCREEN

    private JPanel createQuizScreen() {
        JPanel p = page();

        JPanel header = new JPanel(new BorderLayout(12, 8));
        header.setOpaque(false);

        JButton back = makeButton("← Dashboard", DARK);
        back.setPreferredSize(new Dimension(145, 42));

        back.addActionListener(e -> {
            stopQuizTimer();
            updateDashboardSummary();
            cardLayout.show(root, "dashboard");
        });

        header.add(back, BorderLayout.WEST);

        JPanel info = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 14, 2));
        info.setOpaque(false);

        questionCountLabel = label("Question 1 of 3", 13, MUTED);

        timerLabel = label("Time: 20s", 15, PURPLE_DARK);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

        info.add(questionCountLabel);
        info.add(timerLabel);

        header.add(info, BorderLayout.EAST);

        p.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(12, 12));
        body.setOpaque(false);

        progressBar = new JProgressBar(0, 3);
        progressBar.setValue(1);
        progressBar.setForeground(PURPLE);
        progressBar.setBackground(new Color(229, 223, 247));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(100, 9));

        body.add(progressBar, BorderLayout.NORTH);

        JPanel quizCard = whiteCard();
        quizCard.setLayout(new BorderLayout(12, 15));

        quizCard.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(22, 24, 20, 24)));

        JPanel questionHeader = new JPanel(
                new BorderLayout(8, 8));
        questionHeader.setOpaque(false);

        quizHeadingLabel = label(
                "YOUR CHALLENGE", 11, PURPLE);
        quizHeadingLabel.setFont(
                new Font("SansSerif", Font.BOLD, 11));

        questionLabel = heading(
                "Question text will appear here.", 22);
        questionLabel.setVerticalAlignment(SwingConstants.CENTER);

        questionHeader.add(
                quizHeadingLabel, BorderLayout.NORTH);
        questionHeader.add(
                questionLabel, BorderLayout.CENTER);

        quizCard.add(questionHeader, BorderLayout.NORTH);

        JPanel options = new JPanel(
                new GridLayout(4, 1, 10, 10));
        options.setOpaque(false);

        optionButtons = new JButton[4];

        for (int i = 0; i < optionButtons.length; i++) {

            final int answerIndex = i;

            JButton b = new JButton();

            b.setFont(new Font("SansSerif", Font.PLAIN, 15));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setForeground(DARK);
            b.setBackground(WHITE);
            b.setOpaque(true);
            b.setFocusPainted(false);

            b.setBorder(new CompoundBorder(
                    new LineBorder(BORDER, 1, true),
                    new EmptyBorder(12, 15, 12, 15)));

            b.addActionListener(
                    e -> selectAnswer(answerIndex));

            optionButtons[i] = b;
            options.add(b);
        }

        quizCard.add(options, BorderLayout.CENTER);

        JPanel bottom = new JPanel(
                new BorderLayout(12, 8));
        bottom.setOpaque(false);

        feedbackLabel = label(
                "Select the best answer.", 13, MUTED);

        nextButton = makeButton(
                "Next Question  →", PURPLE);

        nextButton.setPreferredSize(
                new Dimension(180, 44));
        nextButton.setEnabled(false);
        nextButton.addActionListener(e -> nextQuestion());

        bottom.add(feedbackLabel, BorderLayout.CENTER);
        bottom.add(nextButton, BorderLayout.EAST);

        quizCard.add(bottom, BorderLayout.SOUTH);

        body.add(quizCard, BorderLayout.CENTER);

        p.add(body, BorderLayout.CENTER);

        p.add(label(
                "Take your time, focus, and do your best.",
                11, MUTED), BorderLayout.SOUTH);

        return p;
    }

    private void startQuiz() {

        stopQuizTimer();

        List<Question> source =
                questionBank.get(
                        makeKey(selectedSubject, selectedLevel));

        if (source == null || source.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Questions are not available for this selection.");
            return;
        }

        activeQuestions.clear();
        activeQuestions.addAll(source);

        questionIndex = 0;
        score = 0;

        setTitle("QUIZORA | " + selectedSubject
                + " - " + selectedLevel);

        cardLayout.show(root, "quiz");

        showQuestion();
    }

    private void showQuestion() {

        if (questionIndex >= activeQuestions.size()) {
            showResult();
            return;
        }

        Question q = activeQuestions.get(questionIndex);

        selectedAnswer = -1;
        secondsRemaining = 20;

        questionCountLabel.setText(
                "Question " + (questionIndex + 1)
                        + " of " + activeQuestions.size());

        timerLabel.setText(
                "Time: " + secondsRemaining + "s");

        progressBar.setMaximum(activeQuestions.size());
        progressBar.setValue(questionIndex + 1);

        quizHeadingLabel.setText(
                selectedSubject.toUpperCase()
                        + "  •  " + selectedLevel.toUpperCase());

        questionLabel.setText(
                "<html><div style='width:690px'>"
                        + escapeHtml(q.text)
                        + "</div></html>");

        for (int i = 0; i < 4; i++) {

            optionButtons[i].setText(
                    (char) ('A' + i) + "   " + q.options[i]);

            optionButtons[i].setEnabled(true);
            optionButtons[i].setBackground(WHITE);
            optionButtons[i].setForeground(DARK);

            optionButtons[i].setBorder(
                    new CompoundBorder(
                            new LineBorder(BORDER, 1, true),
                            new EmptyBorder(12, 15, 12, 15)));
        }

        feedbackLabel.setForeground(MUTED);
        feedbackLabel.setText("Select one answer to continue.");

        nextButton.setEnabled(false);

        nextButton.setText(
                questionIndex == activeQuestions.size() - 1
                        ? "Finish Quiz  ✓"
                        : "Next Question  →");

        startQuizTimer();
    }

    private void selectAnswer(int index) {

        if (selectedAnswer != -1) {
            return;
        }

        selectedAnswer = index;

        stopQuizTimer();

        Question q = activeQuestions.get(questionIndex);

        if (index == q.correctIndex) {

            score++;

            optionButtons[index].setBackground(GREEN_BG);
            optionButtons[index].setForeground(GREEN);

            feedbackLabel.setForeground(GREEN);
            feedbackLabel.setText("Correct! Great job.");

        } else {

            optionButtons[index].setBackground(RED_BG);
            optionButtons[index].setForeground(RED);

            optionButtons[q.correctIndex].setBackground(GREEN_BG);
            optionButtons[q.correctIndex].setForeground(GREEN);

            feedbackLabel.setForeground(RED);
            feedbackLabel.setText(
                    "Not quite. Correct answer: "
                            + q.options[q.correctIndex]);
        }

        for (JButton b : optionButtons) {
            b.setEnabled(false);
        }

        nextButton.setEnabled(true);
    }

    private void startQuizTimer() {

        stopQuizTimer();

        quizTimer = new javax.swing.Timer(1000, e -> {

            secondsRemaining--;

            timerLabel.setText(
                    "Time: " + secondsRemaining + "s");

            if (secondsRemaining <= 0) {

                stopQuizTimer();

                if (selectedAnswer == -1) {

                    Question q =
                            activeQuestions.get(questionIndex);

                    selectedAnswer = -2;

                    feedbackLabel.setForeground(RED);
                    feedbackLabel.setText(
                            "Time is up. Correct answer: "
                                    + q.options[q.correctIndex]);

                    for (JButton b : optionButtons) {
                        b.setEnabled(false);
                    }

                    optionButtons[q.correctIndex]
                            .setBackground(GREEN_BG);

                    optionButtons[q.correctIndex]
                            .setForeground(GREEN);

                    nextButton.setEnabled(true);
                }
            }
        });

        quizTimer.start();
    }

    private void stopQuizTimer() {

        if (quizTimer != null) {
            quizTimer.stop();
            quizTimer = null;
        }
    }

    private void nextQuestion() {

        if (selectedAnswer == -1) {
            return;
        }

        questionIndex++;

        showQuestion();
    }

    // RESULT SCREEN

    private JPanel createResultScreen() {

        JPanel p = page();

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel result = whiteCard();
        result.setPreferredSize(new Dimension(540, 500));

        result.setLayout(new BoxLayout(
                result, BoxLayout.Y_AXIS));

        result.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(24, 28, 24, 28)));

        JLabel trophy = label("🏆", 48, PURPLE);
        trophy.setHorizontalAlignment(SwingConstants.CENTER);
        trophy.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultTitleLabel = heading("Quiz Completed!", 27);
        resultTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultStudentLabel = label("Great work!", 15, MUTED);
        resultStudentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultScoreLabel = heading("0 / 3", 42);
        resultScoreLabel.setForeground(PURPLE);
        resultScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultPercentLabel = label("0% score", 16, DARK);
        resultPercentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultMessageLabel = label(
                "Keep practising!", 15, DARK);
        resultMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultHighScoreLabel = label(
                "Your high score: 0%", 13, PURPLE_DARK);
        resultHighScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton retry = makeButton("Try Again", PURPLE);
        retry.setAlignmentX(Component.CENTER_ALIGNMENT);
        retry.setMaximumSize(new Dimension(330, 44));
        retry.addActionListener(e -> startQuiz());

        JButton home = makeButton("Back to Dashboard", DARK);
        home.setAlignmentX(Component.CENTER_ALIGNMENT);
        home.setMaximumSize(new Dimension(330, 44));

        home.addActionListener(e -> {
            updateDashboardSummary();
            setTitle("QUIZORA | Welcome, " + studentName);
            cardLayout.show(root, "dashboard");
        });

        result.add(trophy);
        result.add(Box.createVerticalStrut(8));
        result.add(resultTitleLabel);
        result.add(Box.createVerticalStrut(5));
        result.add(resultStudentLabel);
        result.add(Box.createVerticalStrut(17));
        result.add(resultScoreLabel);
        result.add(Box.createVerticalStrut(3));
        result.add(resultPercentLabel);
        result.add(Box.createVerticalStrut(12));
        result.add(resultMessageLabel);
        result.add(Box.createVerticalStrut(6));
        result.add(resultHighScoreLabel);
        result.add(Box.createVerticalGlue());
        result.add(retry);
        result.add(Box.createVerticalStrut(10));
        result.add(home);

        center.add(result);

        p.add(center, BorderLayout.CENTER);

        p.add(label(
                "QUIZORA  •  Every attempt helps you improve.",
                11, MUTED), BorderLayout.SOUTH);

        return p;
    }

    private void showResult() {

        stopQuizTimer();

        int total = activeQuestions.size();

        int percent = total == 0
                ? 0
                : (score * 100) / total;

        String message;

        if (percent >= 80) {
            message = "Excellent! Fantastic work!";
        } else if (percent >= 50) {
            message = "Good job! Keep learning and improving.";
        } else {
            message = "Try one more time! You can do it.";
        }

        saveResult(score, total, percent);

        int bestForSelection =
                getHighScoreForSelection(
                        selectedSubject, selectedLevel);

        resultTitleLabel.setText("Quiz Completed!");

        resultStudentLabel.setText(
                "Great work, " + studentName + "!");

        resultScoreLabel.setText(score + " / " + total);

        resultPercentLabel.setText(percent + "% score");

        resultMessageLabel.setText(message);

        resultHighScoreLabel.setText(
                "High score for " + selectedLevel
                        + ": " + bestForSelection + "%");

        cardLayout.show(root, "result");
    }

    // SAVE SCORE HISTORY

    private void saveResult(int correct, int total, int percent) {

        String date = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        try (PrintWriter out = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(historyFile, true),
                        StandardCharsets.UTF_8))) {

            out.println(
                    date + " | "
                            + safeForHistory(studentName) + " | "
                            + selectedSubject + " | "
                            + selectedLevel + " | "
                            + correct + "/" + total + " | "
                            + percent + "%");

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Your result could not be saved. "
                            + ex.getMessage(),
                    "History error",
                    JOptionPane.WARNING_MESSAGE);
        }

        updateDashboardSummary();
    }

    private String safeForHistory(String value) {

        return value.replace("|", " ")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    private List<String[]> readHistoryRows() {

        List<String[]> rows = new ArrayList<>();

        if (!historyFile.exists()) {
            return rows;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(historyFile),
                        StandardCharsets.UTF_8))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\\s*\\|\\s*");

                if (parts.length >= 6) {
                    rows.add(parts);
                }
            }

        } catch (IOException ignored) {
            // Show empty history if the file cannot be read.
        }

        return rows;
    }

    private String getLastHistoryLine() {

        List<String[]> rows = readHistoryRows();

        if (rows.isEmpty()) {
            return null;
        }

        String[] r = rows.get(rows.size() - 1);

        return r[2] + " (" + r[3] + "): "
                + r[4] + " - " + r[5];
    }

    private int getOverallHighScore() {

        int best = 0;

        for (String[] row : readHistoryRows()) {

            try {

                best = Math.max(
                        best,
                        Integer.parseInt(
                                row[5].replace("%", "").trim()));

            } catch (NumberFormatException ignored) {
                // Ignore an invalid history entry.
            }
        }

        return best;
    }

    private int getHighScoreForSelection(
            String subject, String level) {

        int best = 0;

        for (String[] row : readHistoryRows()) {

            if (row[2].equals(subject)
                    && row[3].equals(level)) {

                try {

                    best = Math.max(
                            best,
                            Integer.parseInt(
                                    row[5].replace("%", "").trim()));

                } catch (NumberFormatException ignored) {
                    // Ignore an invalid history entry.
                }
            }
        }

        return best;
    }

    private void showHistory() {

        List<String[]> rows = readHistoryRows();

        if (rows.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "No quiz attempts yet. Complete a quiz and your score history will appear here.",
                    "My Quiz History",
                    JOptionPane.INFORMATION_MESSAGE);

            return;
        }

        StringBuilder output = new StringBuilder();

        output.append("QUIZORA - QUIZ HISTORY\n");

        output.append(
                "---------------------------------------------------------------\n");

        for (String[] row : rows) {

            output.append(row[0]).append(" | ")
                    .append(row[2]).append(" | ")
                    .append(row[3]).append(" | Score: ")
                    .append(row[4]).append(" | ")
                    .append(row[5]).append("\n");
        }

        JTextArea area = new JTextArea(
                output.toString(), 18, 62);

        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setForeground(DARK);
        area.setBackground(BG);
        area.setCaretPosition(0);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(700, 360));

        JOptionPane.showMessageDialog(
                this,
                scroll,
                "My Quiz History",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // HTML TEXT HELPER

    private String escapeHtml(String text) {

        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    // PROGRAM ENTRY POINT

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());

            } catch (Exception ignored) {
                // Use the default look and feel if needed.
            }

            new Quizora();
        });
    }
}