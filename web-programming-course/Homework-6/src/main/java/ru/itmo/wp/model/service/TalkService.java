package ru.itmo.wp.model.service;

import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.TalkRepository;
import ru.itmo.wp.model.repository.impl.TalkRepositoryImpl;

import java.util.List;

public class TalkService {
    TalkRepository talkRepository = new TalkRepositoryImpl();

    public void validateSend(String targetUserId) throws ValidationException {
        if (targetUserId == null) {
            throw new ValidationException("Target user must be selected");
        }
    }

    public void saveTalk(long sourceUserId, long targetUserId, String text) {
        Talk talk = new Talk();
        talk.setSourceId(sourceUserId);
        talk.setTargetId(targetUserId);
        talk.setText(text);
        talkRepository.save(talk);
    }

    public List<Talk> findAllByUserId(long userId) {
        return talkRepository.findAllByUserId(userId);
    }
}
