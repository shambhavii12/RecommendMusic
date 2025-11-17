package mycollegeproject.musicworld;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicRecommendation {
    private JFrame frame;
    private JComboBox<String> categoryBox, choiceBox;
    private JLabel resultLabel;
    private JButton recommendButton;
    
    private static final String API_KEY = "7056f3cc918923d44f17b668f4cab1d0"; // Replace with your API key
    private static final String LASTFM_API_URL = "http://ws.audioscrobbler.com/2.0/";

    public MusicRecommendation() {
        // Create GUI Frame
        frame = new JFrame("🎧 Music Recommendation System");
        frame.setSize(550, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.getContentPane().setBackground(new Color(30, 30, 40)); // Dark mode background

        // Title Label
        JLabel titleLabel = new JLabel("🎵 Music Recommendation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(100, 20, 350, 30);

        // Category Drop down
        categoryBox = createStyledDropdown(new String[]{"Mood", "Genre"});
        categoryBox.setBounds(50, 80, 200, 30);
        categoryBox.addActionListener(e -> updateChoices());

        // Choice Drop down
        choiceBox = createStyledDropdown(new String[]{});
        choiceBox.setBounds(300, 80, 200, 30);
        updateChoices();

        // Recommendation Button
        recommendButton = new JButton("🎶 Get Recommendation");
        styleButton(recommendButton);
        recommendButton.setBounds(175, 130, 200, 40);
        recommendButton.addActionListener(e -> showRecommendation());

        // Result Label
        resultLabel = new JLabel("<html>Select a category & choice, then press the button!</html>", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setBounds(50, 180, 450, 100);

        // Add Components to Frame
        frame.add(titleLabel);
        frame.add(categoryBox);
        frame.add(choiceBox);
        frame.add(recommendButton);
        frame.add(resultLabel);

        // Show Frame
        frame.setVisible(true);
    }

    private JComboBox<String> createStyledDropdown(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setForeground(Color.WHITE);
        comboBox.setBackground(new Color(50, 50, 60));
        comboBox.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return comboBox;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 180, 250));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
    }

    private void updateChoices() {
        choiceBox.removeAllItems();
        String category = (String) categoryBox.getSelectedItem();

        if (category.equals("Mood")) {
            String[] moods = {"Happy", "Sad", "Energetic", "Relaxed"};
            for (String mood : moods) {
                choiceBox.addItem(mood);
            }
        } else {
            String[] genres = {"Rock", "Pop", "Jazz", "Classical"};
            for (String genre : genres) {
                choiceBox.addItem(genre);
            }
        }
    }

    private void showRecommendation() {
        String category = (String) categoryBox.getSelectedItem();
        String choice = (String) choiceBox.getSelectedItem();
        String query = category.equals("Mood") ? choice + " music" : choice;

        try {
            String urlString = LASTFM_API_URL + "?method=track.search&track=" + query + 
                               "&api_key=" + API_KEY + "&format=xml";
            String response = sendGetRequest(urlString);

            // Extract song name and artist using Regex
            Pattern pattern = Pattern.compile("<track>.*?<name>(.*?)</name>.*?<artist>.*?<name>(.*?)</name>", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(response);

            if (matcher.find()) {
                String song = matcher.group(1);
                String artist = matcher.group(2);

                // Generate a clickable YouTube search link
                String youtubeLink = "https://www.youtube.com/results?search_query=" +
                                     song.replace(" ", "+") + "+" + artist.replace(" ", "+");

                resultLabel.setText("<html><b>🎶 " + song + " - " + artist + "</b><br>" +
                                    "👉 <a href='" + youtubeLink + "'>Listen Here</a></html>");
            } else {
                resultLabel.setText("<html>No recommendation found.</html>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching recommendation: " + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
	private String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("API Request Failed. HTTP Code: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        
        in.close();
        return response.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MusicRecommendation::new);
    }
}
