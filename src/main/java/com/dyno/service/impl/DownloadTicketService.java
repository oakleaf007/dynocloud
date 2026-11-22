
package com.dyno.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dyno.dto.DownloadTicket;
import com.dyno.entity.User;
import com.dyno.execption.ResourceNotFoundException;
import com.dyno.repository.DownloadTicketRepository;
import com.dyno.service.FileService;

@Service
public class DownloadTicketService {

    private final DownloadTicketRepository repository;
    private final FileService fileService;

    private final SecureRandom secureRandom = new SecureRandom();

    public DownloadTicketService(
            DownloadTicketRepository repository,
            FileService fileService) {

        this.repository = repository;
        this.fileService = fileService;
    }

    @Transactional
    public String createTicket(UUID fileId, User user) {

      
        fileService.prepareStreamingDownload(fileId, user);

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String rawTicket = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        String hash = hashToken(rawTicket);

        DownloadTicket ticket = new DownloadTicket(
                user,
                fileId,
                hash,
                Instant.now().plus(2, ChronoUnit.MINUTES)
        );

        repository.save(ticket);

        
        return rawTicket;
    }

    @Transactional
    public DownloadTicket consumeTicket(String rawTicket) {

        String hash = hashToken(rawTicket);

        DownloadTicket ticket = repository.findValidTicket(
                hash,
                Instant.now()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Invalid or expired download ticket"
                )
        );

        ticket.markAsUsed();

        return ticket;
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Unable to hash download ticket", ex
            );
        }
    }
}
