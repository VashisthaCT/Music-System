import java.util.*;
import java.util.stream.Collectors;


//Custom exception to be thrown when attempting to add a song
class SongAlreadyExistsException extends Exception {
    public SongAlreadyExistsException(String message) {
        super(message);
    }
}

class SongNotFoundException extends Exception {
    public SongNotFoundException(String message) {
        super(message);
    }
}

class Song {
    private String name;
    private String artist;
    private int totalPlays;
    private Map<String, Integer> datePlays; // Key: date, Value: # of plays on that date

    public Song(String name, String artist) {
        this.name = name;
        this.artist = artist;
        this.totalPlays = 0;
        this.datePlays = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public int getTotalPlays() {
        return totalPlays;
    }

    public Map<String, Integer> getDatePlays() {
        return datePlays;
    }

    //Increments the total play count and the count for a specific date.
    public void incrementPlayCount(String date) {
        this.totalPlays += 1;
        datePlays.put(date, datePlays.getOrDefault(date, 0) + 1);
    }

    @Override
    public String toString() {
        return String.format("Song{name='%s', artist='%s', totalPlays=%d}", name, artist, totalPlays);
    }
}

class SongManager {
    private Map<String, Song> songMap;

    public SongManager() {
        this.songMap = new HashMap<>();
    }

    public void addSong(String songName, String artist) throws SongAlreadyExistsException {
        String key = generateKey(songName, artist);

        if (songMap.containsKey(key)) {
            throw new SongAlreadyExistsException(
                    "Song '" + songName + "' by '" + artist + "' already exists."
            );
        }

        songMap.put(key, new Song(songName, artist));
    }

    
    public void playSong(String songName, String artist, String date) throws SongNotFoundException {
        String key = generateKey(songName, artist);
        Song song = songMap.get(key);

        if (song == null) {
            throw new SongNotFoundException(
                    "Cannot play. Song '" + songName + "' by '" + artist + "' not found."
            );
        }

        song.incrementPlayCount(date);
    }

    public List<Song> top10SongsOverall() {
        return songMap.values().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getTotalPlays(), s1.getTotalPlays()))
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Song> top10SongsByArtist(String artist) {
        return songMap.values().stream()
                .filter(song -> song.getArtist().equalsIgnoreCase(artist))
                .sorted((s1, s2) -> Integer.compare(s2.getTotalPlays(), s1.getTotalPlays()))
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Song> top10SongsByDate(String date) {
        return songMap.values().stream()
                // Sort by the number of plays on the specified date
                .sorted((s1, s2) -> Integer.compare(
                        s2.getDatePlays().getOrDefault(date, 0),
                        s1.getDatePlays().getOrDefault(date, 0)))
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Song> songsPlayedLessThanFiveTimes() {
        return songMap.values().stream()
                .filter(song -> song.getTotalPlays() < 5)
                .collect(Collectors.toList());
    }

    private String generateKey(String songName, String artist) {
        return songName.toLowerCase() + "-" + artist.toLowerCase();
    }
}

public class Main {
    public static void main(String[] args) {
        SongManager manager = new SongManager();

        try {
            // 1. Add Songs
            manager.addSong("Let It Be", "The Beatles");
            manager.addSong("Yesterday", "The Beatles");
            manager.addSong("Shape of You", "Ed Sheeran");
            manager.addSong("Blinding Lights", "The Weeknd");
            manager.addSong("Despacito", "Luis Fonsi");
            manager.addSong("Believer", "Imagine Dragons");

            // 2. Play some songs on different dates
            manager.playSong("Let It Be", "The Beatles", "2025-01-01");
            manager.playSong("Let It Be", "The Beatles", "2025-01-01");
            manager.playSong("Let It Be", "The Beatles", "2025-01-02");

            manager.playSong("Yesterday", "The Beatles", "2025-01-01");
            manager.playSong("Yesterday", "The Beatles", "2025-01-02");
            manager.playSong("Yesterday", "The Beatles", "2025-01-02");

            manager.playSong("Shape of You", "Ed Sheeran", "2025-01-02");
            manager.playSong("Shape of You", "Ed Sheeran", "2025-01-03");
            manager.playSong("Shape of You", "Ed Sheeran", "2025-01-04");
            manager.playSong("Shape of You", "Ed Sheeran", "2025-01-05");
            // "Shape of You" has 4 plays

            manager.playSong("Blinding Lights", "The Weeknd", "2025-01-02");
            manager.playSong("Despacito", "Luis Fonsi", "2025-01-02");
            // "Believer" by Imagine Dragons not played yet -> 0 plays

            // 3. Top 10 Songs Overall
            System.out.println("=== Top 10 Songs Overall ===");
            manager.top10SongsOverall().forEach(System.out::println);

            // 4. Top 10 Songs for The Beatles
            System.out.println("\n=== Top 10 Songs for The Beatles ===");
            manager.top10SongsByArtist("The Beatles").forEach(System.out::println);

            // 5. Top 10 Songs for a given date (e.g., "2025-01-01")
            System.out.println("\n=== Top 10 Songs for 2025-01-01 ===");
            manager.top10SongsByDate("2025-01-01").forEach(System.out::println);

            // 6. Songs played less than five times
            System.out.println("\n=== Songs played less than five times (overall) ===");
            manager.songsPlayedLessThanFiveTimes().forEach(System.out::println);

            // Trying to add a duplicate song (will trigger exception):
            manager.addSong("Let It Be", "The Beatles"); 
        } catch (SongAlreadyExistsException | SongNotFoundException e) {
            // Catch either our custom exceptions
            System.err.println("Exception: " + e.getMessage());
        }

        // Attempt to play a non-existent song (will trigger SongNotFoundException)
        try {
            manager.playSong("Non Existent Song", "Unknown Artist", "2025-01-01");
        } catch (SongNotFoundException e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
}