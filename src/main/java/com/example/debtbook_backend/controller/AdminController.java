package com.example.debtbook_backend.controller;


import com.example.debtbook_backend.bot.DebtBook;
import com.example.debtbook_backend.entity.Attachment;
import com.example.debtbook_backend.entity.BotUser;
import com.example.debtbook_backend.entity.News;
import com.example.debtbook_backend.projection.CustomDebtor;
import com.example.debtbook_backend.projection.DebtorProjection;
import com.example.debtbook_backend.projection.StoreMessageProjection;
import com.example.debtbook_backend.projection.UserProjection;
import com.example.debtbook_backend.repository.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final StoreRepository storeRepository;
    private final DebtorRepository debtorRepository;
    private final DebtRepository debtRepository;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;
    private final NewsRepository newsRepository;
    private final DebtBook debtBook;

    @Autowired
    public AdminController(
            StoreRepository storeRepository,
            DebtorRepository debtorRepository,
            DebtRepository debtRepository,
            UserRepository userRepository,
            AttachmentRepository attachmentRepository,
            NewsRepository newsRepository,
            DebtBook debtBook
    ) {
        this.storeRepository = storeRepository;
        this.debtorRepository = debtorRepository;
        this.debtRepository = debtRepository;
        this.userRepository = userRepository;
        this.attachmentRepository = attachmentRepository;
        this.newsRepository = newsRepository;
        this.debtBook = debtBook;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/store")
    public Page<UserProjection> getStores(
            @RequestParam Integer page,
            @RequestParam(defaultValue = "") String search
    ) {


        return userRepository.getAllUsersStore(PageRequest.of(page, 10), search);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/debtor")
    public Page<CustomDebtor> getDebtors(
            @RequestParam Integer page,
            @RequestParam(defaultValue = "") String search
    ) {
        return debtorRepository.getAllDebtors(PageRequest.of(page, 10), search);


    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/store-debtors")
    public Page<DebtorProjection> getStoreDebtors(
            @RequestParam Integer page,
            @RequestParam UUID storeId) {
        return debtorRepository.getStoreDebtor(PageRequest.of(page, 10), storeId);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SneakyThrows
    @GetMapping("/store/img/{id}")
    public void getFile(@PathVariable UUID id, HttpServletResponse response) {
        Attachment attachment = attachmentRepository.findById(id).get();
        FileCopyUtils.copy(attachment.getFile(), response.getOutputStream());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SneakyThrows
    @PostMapping("/store-image")
    public UUID saveFileMessage(@RequestParam MultipartFile file) {
        Attachment attachment = new Attachment();
        attachment.setFile(file.getBytes());
        Attachment save = attachmentRepository.save(attachment);
        return save.getId();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SneakyThrows
    @PostMapping("/storemessage")
    public News sendMessageToStoreOwners(@RequestBody StoreMessageProjection message) {

        News news = new News();
        news.setDescription(message.getDescription());
        Optional<Attachment> byId = attachmentRepository.findById(message.getAttachmentId());
        if (byId.isPresent()) {
            news.setAttachment(byId.get());
            for (BotUser botUser : userRepository.getAllU()) {
                debtBook.sendMessageToStoreOwners(byId.get(), message.getDescription(), botUser.getChatId());
            }
        }

        return newsRepository.save(news);
    }


}
