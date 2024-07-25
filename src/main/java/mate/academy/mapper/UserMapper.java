package mate.academy.mapper;

import mate.academy.dto.UserRegistrationRequestDto;
import mate.academy.dto.UserResponseDto;
import mate.academy.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toModel(UserRegistrationRequestDto requestDto);

    @Mapping(source = "shippingAddress", target = "shippingAddress")
    UserResponseDto toDto(User user);
}
