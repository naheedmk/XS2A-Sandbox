package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    public void uploadUserTransaction(File file) {
        List<UserTransaction> transactions = readUserTransaction(file);
    }

    private List<UserTransaction> readUserTransaction(File file) {
        try (Reader reader = new FileReader(file)) {
            CsvToBean<UserTransaction> csvToBean = new CsvToBeanBuilder<UserTransaction>(reader)
                                                       .withType(UserTransaction.class)
                                                       .withIgnoreLeadingWhiteSpace(true)
                                                       .build();
            return csvToBean.parse();
        } catch (IOException e) {
            log.error("Can't read transactions", e);
            throw new IllegalArgumentException("Can't read transactions");
        }
    }
}
