package br.com.pinalli.screenmatch;

import br.com.pinalli.screenmatch.model.DataSerie;
import br.com.pinalli.screenmatch.model.EpisodesData;
import br.com.pinalli.screenmatch.services.APIconsume;
import br.com.pinalli.screenmatch.services.DataConvert;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var consumeApi = new APIconsume();
		var json = consumeApi.getData("https://www.omdbapi.com/?t=gilmore+girls&apikey=6585022c");
		//System.out.println("DATA: " + json);
		//json = consumeApi.getData("https://coffee.alexflipnote.dev/random.json");
		System.out.println("Data: " + json);
		DataConvert convert = new DataConvert();
		DataSerie data = convert.getData(json, DataSerie.class);
		System.out.println("Data Series: " + data);
		json = consumeApi.getData("https://www.omdbapi.com/?t=gilmore+girls&season=1&episode=2&apikey=6585022c");
		EpisodesData episodes = convert.getData(json, EpisodesData.class);
		System.out.println("Episodes: " + episodes);
	}
}
