package org.ajou.multichatserver.channel.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static org.ajou.multichatserver.common.ErrorCode.MISSING_REQUEST_PARAMETER;
import static org.springframework.util.StringUtils.hasText;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ajou.multichatserver.common.BaseEntity;
import org.ajou.multichatserver.common.exception.InvalidRequestException;

@Entity
@Table(name = "channel")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Slf4j
public class Channel extends BaseEntity {
    public static final String defaultName = "일반";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "channel_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    public Channel(String name) {
        if (!hasText(name)) {
            throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
        }
        this.name = name;
    }
}
