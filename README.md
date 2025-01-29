# ScreenMatch - TV Series Information App

## 📋 Overview

### ScreenMatch is a Java application that allows users to search for TV series information using the OMDB API. The application provides details about TV series, including seasons, episodes, and ratings.

## 🚀 Features

- Search for TV series by name
- Display comprehensive series information
- View all seasons and episodes
- Show top 5 highest-rated episodes
- Error handling for invalid searches
- User-friendly command-line interface

##  🛠️ Technologies

- ☕ Java 21
- Stream API
- Lambda expressions
- OMDB API integration
  
## ⚙️ Prerequisites

- Java Development Kit (JDK) 17 or higher
- An OMDB API key

## 📥 Installation and Configuration

### 1. Clone the repository
```
git clone https://github.com/Pinalli/screenmatch
````
### 2. Navigate to the project directory
```
cd ScreenMatch
```
### 3. Configure your OMDB API key in the application.
To use the OMDB API, you need an API key:

1. Get an API key from [OMDB API](https://www.omdbapi.com/).
2. Add your API key to the application configuration file or as an environment variable.

## 📖 Usage
### Run the application

1. Enter the name of a TV series.
2. View details about the series, including:
   - Seasons
   - Episodes
   - Top ratings
   - Explore the top 5 highest-rated episodes
   - Average rating per season
   - Find episode by title
## Code Examples 👨‍💻
### Fetching Season Data
```java
private List<SeasonData> fetchAllSeasons(APIconsume consumeApi, DataConvert convert,
                                       Integer totalSeasons, String serieName) {
    if (totalSeasons == null || totalSeasons <= 0) {
        throw new IllegalArgumentException("Invalid number of seasons");
    }

    return IntStream.rangeClosed(1, totalSeasons)
            .mapToObj(i -> {
                try {
                    String json = consumeApi.getData(BASE_URL +
                            serieName.replace(" ", "+") + "&season=" + i + API_KEY);
                    return convert.getData(json, SeasonData.class);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toList());
}
```
### Finding Top Episodes
```java
   public void findTop5Episodes(List<SeasonData> seasonsDate) {
        List<EpisodesData> episodeDataList = seasonsDate.stream()
                .flatMap(t -> t.episodes().stream())
                .toList();

        System.out.println("\nTOP FIVE EPISODES:");
        episodeDataList.stream()
                .filter(e -> !e.assessment().equalsIgnoreCase("N/A "))               
                .sorted(Comparator.comparing(EpisodesData::assessment).reversed())              
                .limit(5)
                .map(e -> e.title().toUpperCase())            
                .forEach(System.out::println);
    }
```
### Average rating per season
```java
    public void  averageAssessmentBySeason(List<Episode> episodes) {
        System.out.println("\nAVARAGE ASSESSMENT BY SEASON");
        Map<Integer, Double> assessmentForSeason = episodes.stream()
                .filter(e -> e.getAssessment() > 0.0)
                .collect(Collectors.groupingBy(Episode::getSeason,
                        Collectors.averagingDouble(Episode::getAssessment)));
        System.out.println(assessmentForSeason);

        DoubleSummaryStatistics stats = episodes.stream()
                .filter(e -> e.getAssessment() > 0.0)
                .collect(Collectors.summarizingDouble(Episode::getAssessment));
        System.out.println("\nAVARAGE " + stats.getAverage() + "\nWORSE EPISODE " + stats.getMin() + " \nBEST EPISODE " + stats.getMax() + "\nTOTAL " + stats.getCount());

    }

```
### Find episode by title
```java
    public void findEpisodeByTitle(List<Episode> episodes) {
        System.out.println("\nEnter an excerpt from the title of the episode you want to search for:");
        String titleExcerpt = reader.nextLine();

        Optional<Episode> foundEpisode = episodes.stream()
                .filter(e -> e.getTitle().toUpperCase().contains(titleExcerpt.toUpperCase()))
                .findFirst();

        if (foundEpisode.isPresent()) {
            Episode episode = foundEpisode.get();
            System.out.println("EPISODE FOUND:\n" +
                    "Season: " + episode.getSeason() +
                    ", Title: '" + episode.getTitle() + '\'' +
                    ", Episode Number: " + episode.getEpisodeNumber() +
                    ", Assessment: " + episode.getAssessment() +
                    ", Date Release: " + episode.getDateRelease());
        } else {
            System.out.println("EPISODE NOT FOUND!.");
        }
    }

```

## Contributing 🤝
 Contributions are welcome! Please feel free to submit a Pull Request.
## License 📝
This project is licensed under the MIT License - see the LICENSE file for details.

## Contact📬
For any inquiries, reach out at betopinalli@gmail.com

