package br.com.pinalli.screenmatch;

import br.com.pinalli.screenmatch.main.Main;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AllArgsConstructor
@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

    // Constantes para configurar a API base e a chave de acesso
    private static final String BASE_URL = "https://www.omdbapi.com/?t=gilmore+girls&season=";
    private static final String API_KEY = "&apikey=6585022c";

    public static void main(String[] args) {
        SpringApplication.run(ScreenmatchApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Main main = new Main();
        main.displayMenu();
    }
}

