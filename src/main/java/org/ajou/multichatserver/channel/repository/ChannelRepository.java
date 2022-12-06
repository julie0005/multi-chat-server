package org.ajou.multichatserver.channel.repository;

import org.ajou.multichatserver.channel.domain.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
}
