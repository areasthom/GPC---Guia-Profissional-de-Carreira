package com.gpc.config;

import com.gpc.model.Papel;
import com.gpc.repository.PapelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PapelRepository papelRepository;

    public DataInitializer(PapelRepository papelRepository) {
        this.papelRepository = papelRepository;
    }

    @Override
    public void run(String... args) {
        criarPapel("ALUNO");
        criarPapel("PROFESSOR");
        criarPapel("TUTOR");
        criarPapel("RECRUTADOR");
    }

    private void criarPapel(String nome) {
        if (papelRepository.findByNome(nome).isEmpty()) {
            Papel papel = new Papel();
            papel.setNome(nome);
            papelRepository.save(papel);
        }
    }
}