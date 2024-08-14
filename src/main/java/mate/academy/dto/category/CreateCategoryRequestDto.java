package mate.academy.dto.category;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateCategoryRequestDto {
    private String name;
    private String description;
}
