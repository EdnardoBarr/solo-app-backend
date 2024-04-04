package ednardo.api.soloapp.repository;

import ednardo.api.soloapp.model.Friendship;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;

public interface FriendshipRepository extends JpaAttributeConverter<Friendship, Long> {

}
