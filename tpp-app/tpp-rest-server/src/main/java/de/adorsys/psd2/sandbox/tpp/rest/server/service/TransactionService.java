package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final ParseService parseService;

    public void uploadUserTransaction(MultipartFile multipart) {
        readUserTransaction(parseService.convertMultiPartToFile(multipart));
    }

    private List<UserTransaction> readUserTransaction(File file) {
        return parseService.convertFileToTargetObject(file, UserTransaction.class);
    }
}
