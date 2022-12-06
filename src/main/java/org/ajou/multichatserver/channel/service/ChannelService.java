package org.ajou.multichatserver.channel.service;

import static org.ajou.multichatserver.common.ErrorCode.CHANNEL_NOT_FOUND;

import lombok.RequiredArgsConstructor;
import org.ajou.multichatserver.channel.domain.Channel;
import org.ajou.multichatserver.channel.repository.ChannelRepository;
import org.ajou.multichatserver.common.exception.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;

    @Transactional(readOnly = true)
    public Channel getChannelById(Long channelId) {
        System.out.println(channelId);
        return channelRepository.findById(channelId).orElseThrow(() -> new NoSuchElementException(
                CHANNEL_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public void validateChannelId(Long channelId) {
        if (!channelRepository.existsById(channelId)) throw new NoSuchElementException(CHANNEL_NOT_FOUND);
    }

    @Transactional
    public Channel createChannel(String name) {
        Channel newChannel = new Channel(name);
        return channelRepository.save(newChannel);
    }
}
