package mate.academy.config;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;

@org.mapstruct.MapperConfig(
        componentModel = "spring",
        injectionStrategy = CONSTRUCTOR,
        nullValueCheckStrategy = ALWAYS,
        implementationName = "<CLASS_NAME>Impl"
)
public class MapperConfig {
}
