package mate.academy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {
    private Long bookId;
    private int quantity;
}
