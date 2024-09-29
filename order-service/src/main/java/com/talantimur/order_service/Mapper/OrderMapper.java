package com.talantimur.order_service.Mapper;

import com.talantimur.order_service.dto.OrderLineItemsDto;
import com.talantimur.order_service.model.OrderLineItems;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderLineItems toOrderLineItems(OrderLineItemsDto dto);
}

