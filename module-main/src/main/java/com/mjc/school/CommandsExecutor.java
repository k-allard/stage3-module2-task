package com.mjc.school;

import com.mjc.school.controller.BaseController;
import com.mjc.school.service.dto.AuthorRequestDto;
import com.mjc.school.service.dto.AuthorResponseDto;
import com.mjc.school.service.dto.NewsRequestDto;
import com.mjc.school.service.dto.NewsResponseDto;
import com.mjc.school.service.exceptions.ValidatorException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.mjc.school.service.exceptions.ExceptionsCodes.VALIDATE_INT_VALUE;

@Component
public class CommandsExecutor {

    // TODO organize communication between module-web and module-main
    //  by custom annotations @CommandHandler, @CommandBody and @CommandParam placed in controllers
    private final BaseController<NewsRequestDto, NewsResponseDto, Long> newsController;
    private final BaseController<AuthorRequestDto, AuthorResponseDto, Long> authorController;

    private final TerminalCommandsReader commandsReader = new TerminalCommandsReader();

    public CommandsExecutor(@Qualifier("newsController")
                            BaseController<NewsRequestDto, NewsResponseDto, Long> newsController,
                            @Qualifier("authorController")
                            BaseController<AuthorRequestDto, AuthorResponseDto, Long> authorController) {
        this.newsController = newsController;
        this.authorController = authorController;
    }

    public void executeCommand(Command command) {

        if (command == Command.EXIT)
            System.exit(0);

        System.out.print("Operation: ");
        System.out.println(command.description);
        switch (command) {
            case GET_ALL_NEWS -> {
                for (NewsResponseDto news : newsController.readAll()) {
                    System.out.println(news);
                }
            }
            case GET_NEWS_BY_ID -> System.out.println(
                    newsController.readById(
                            requestNewsId()
                    ));
            case CREATE_NEWS -> System.out.println(
                    newsController.create(
                            new NewsRequestDto(
                                    null,
                                    requestNewsTitle(),
                                    requestNewsContent(),
                                    requestAuthorId()
                            )));
            case UPDATE_NEWS -> System.out.println(
                    newsController.update(
                            new NewsRequestDto(
                                    requestNewsId(),
                                    requestNewsTitle(),
                                    requestNewsContent(),
                                    requestAuthorId()
                            )));
            case REMOVE_NEWS_BY_ID -> System.out.println(
                    newsController.deleteById(
                            requestNewsId()
                    ));
            case GET_ALL_AUTHORS -> {
                for (AuthorResponseDto author : authorController.readAll()) {
                    System.out.println(author);
                }
            }
            case GET_AUTHOR_BY_ID -> System.out.println(
                    authorController.readById(
                            requestAuthorId()
                    )
            );
            case CREATE_AUTHOR -> System.out.println(
                    authorController.create(new AuthorRequestDto(
                            null,
                            requestAuthorName()
                    ))
            );
            case UPDATE_AUTHOR -> System.out.println(
                    authorController.update(new AuthorRequestDto(
                            requestAuthorId(),
                            requestAuthorName()
                    ))
            );
            case REMOVE_AUTHOR_BY_ID -> System.out.println(
                    authorController.deleteById(
                            requestAuthorId())
            );
        }
    }

    private long requestNewsId() {
        try {
            return Long.parseLong(commandsReader.requestResponseByPrompt("Enter news id:"));
        } catch (NumberFormatException e) {
            throw new ValidatorException(
                    String.format(VALIDATE_INT_VALUE.getMessage(), "News id"));
        }
    }

    private long requestAuthorId() {
        try {
            return Long.parseLong(commandsReader.requestResponseByPrompt("Enter author id:"));
        } catch (NumberFormatException e) {
            throw new ValidatorException(
                    String.format(VALIDATE_INT_VALUE.getMessage(), "Author id"));
        }
    }

    private String requestNewsContent() {
        return commandsReader.requestResponseByPrompt("Enter news content:");
    }

    private String requestNewsTitle() {
        return commandsReader.requestResponseByPrompt("Enter news title:");
    }

    private String requestAuthorName() {
        return commandsReader.requestResponseByPrompt("Enter author name:");
    }

}
