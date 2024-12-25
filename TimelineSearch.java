import java.util.*;

public class TimelineSearch {
    private List<TimelineEvent> timeline;

    public static class TimelineEvent implements Comparable<TimelineEvent> {
        private String eventDescription;
        private int timeStamp;  // Military time format (e.g., 2200 for 10:00 PM)
        private String location;
        private int reliability;
        private String[] keywords;

        public TimelineEvent(String eventDescription, int timeStamp, String location, int reliability, String[] keywords) {
            this.eventDescription = eventDescription;
            this.timeStamp = timeStamp;
            this.location = location;
            this.reliability = reliability;
            this.keywords = keywords;
        }

        @Override
        public int compareTo(TimelineEvent other) {
            return Integer.compare(this.timeStamp, other.timeStamp);
        }

        public boolean containsKeyword(String keyword) {
            keyword = keyword.toLowerCase();
            for (String k : keywords) {
                if (k.toLowerCase().contains(keyword)) {
                    return true;
                }
            }
            return eventDescription.toLowerCase().contains(keyword);
        }

        public String toString() {
            String time = String.format("%04d", timeStamp);
            return String.format("[%s:%s] %s (Reliabilitas: %d/10) - Lokasi: %s",
                time.substring(0, 2), time.substring(2), eventDescription, reliability, location);
        }

        public int getTimeStamp() {
            return timeStamp;
        }

        public String getLocation() {
            return location;
        }

        public int getReliability() {
            return reliability;
        }
    }

    public TimelineSearch() {
        timeline = new ArrayList<>();
        initializeTimeline();
    }

    private void initializeTimeline() {
        // Initialize with known events in chronological order
        addEvent("Joko mengambil dokumen penelitian", 1400, "Research Center", 9,
                new String[]{"dokumen", "penelitian", "Joko"});
        addEvent("Joko terlihat gelisah di perpustakaan", 1530, "Perpustakaan", 8,
                new String[]{"gelisah", "perpustakaan", "Joko"});
        addEvent("Pertemuan dengan Pak Santoso", 1600, "Research Center", 9,
                new String[]{"pertemuan", "Pak Santoso"});
        addEvent("Suara pertengkaran terdengar", 1845, "Sistem Informasi", 7,
                new String[]{"pertengkaran", "suara"});
        addEvent("Saksi melihat seseorang berlari", 1900, "Teknik Informatika", 6,
                new String[]{"berlari", "saksi"});
        addEvent("Dokumen hilang dilaporkan", 1930, "Research Center", 8,
                new String[]{"dokumen", "hilang"});
        addEvent("Panggilan telepon mencurigakan", 2000, "Dr. Angka", 5,
                new String[]{"telepon", "panggilan"});
        addEvent("Joko terakhir terlihat", 2100, "Sistem Informasi", 9,
                new String[]{"Joko", "terakhir"});
        addEvent("Suara gaduh dari gudang", 2200, "Sistem Informasi", 7,
                new String[]{"gudang", "suara"});
        addEvent("Penemuan tubuh korban", 2300, "Sistem Informasi", 10,
                new String[]{"korban", "tubuh", "penemuan"});

        // Sort timeline by timestamp
        Collections.sort(timeline);
    }

    public void addEvent(String description, int time, String location, int reliability, String[] keywords) {
        timeline.add(new TimelineEvent(description, time, location, reliability, keywords));
    }

    // Binary search for events by timestamp
    public TimelineEvent findEventByTime(int targetTime) {
        int left = 0;
        int right = timeline.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            TimelineEvent midEvent = timeline.get(mid);

            if (midEvent.getTimeStamp() == targetTime) {
                return midEvent;
            }

            if (midEvent.getTimeStamp() < targetTime) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null;
    }

    // Get timeline events sorted by reliability
    public List<TimelineEvent> getEventsSortedByReliability() {
        List<TimelineEvent> sortedEvents = new ArrayList<>(timeline);
        Collections.sort(sortedEvents, (a, b) -> Integer.compare(b.getReliability(), a.getReliability()));
        return sortedEvents;
    }
}