package ednardo.api.soloapp.model.dto;

import ednardo.api.soloapp.enums.FriendshipStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestFriendshipDTO {
    private Long fromId;
    private Long toId;
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    public RequestFriendshipDTO(Long fromId, Long toId, FriendshipStatus status) {
        this.fromId = fromId;
        this.toId = toId;
        this.status = status;
    }
}
