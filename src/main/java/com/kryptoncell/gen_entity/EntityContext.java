package com.kryptoncell.gen_entity;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EntityContext {

    private final List<EntityMetadata> entityMetadataList = new ArrayList<>();

}
