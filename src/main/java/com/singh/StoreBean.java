package com.singh;

import com.singh.entity.Inventory;
import com.singh.entity.Store;
import com.singh.interceptor.Logged;
import com.singh.inventory.InventoryService;
import com.singh.store.StoreService;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RequestScoped
@Named
public class StoreBean {

    private Long id;

    @Size(min = 1, max = 10)
    private String name;

    @NotEmpty
    private String location;

    @Size(min = 0)
    private List<Long> addInventoryIds = new ArrayList<>();


    @Size(min = 0)
    private List<Long> updateInventoryIds = new ArrayList<>();

    @EJB
    private StoreService storeService;

    @EJB
    private InventoryService inventoryService;

    @Logged
    public void addStore() {
        storeService.addStore(buildStore());
    }

    @Logged
    public void updateStore() {
        System.out.println();
        System.out.println(id);
        System.out.println(updateInventoryIds);
        storeService.updateStore(id, updateInventoryIds);
    }

    private Store buildStore() {
        return Store.builder().id(id).name(name).location(location).inventoryList(
                addInventoryIds.stream().map(id -> inventoryService.getInventoryById(id)).collect(toList())
        ).build();
    }

    public List<Store> getStores() {
        return storeService.getAllStores();
    }

    public List<Inventory> getUnSelectedInventories() {
        Set<Long> ids = getStores().stream().flatMap(store -> storeService.getStoreById(store.getId()).getInventoryList().stream())
                .map(Inventory::getId).collect(Collectors.toSet());
        return inventoryService.getInventoryList().stream().filter(in -> !ids.contains(in.getId())).collect(toList());
    }

    public void onInventoryChange() {
        if (id == null) {
            updateInventoryIds.clear();
            return;
        }
        updateInventoryIds = storeService.getStoreById(id).getInventoryList().stream().map(Inventory::getId).collect(toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Long> getAddInventoryIds() {
        return addInventoryIds;
    }

    public List<Long> getUpdateInventoryIds() {
        return updateInventoryIds;
    }

    public void setAddInventoryIds(List<Long> addInventoryIds) {
        this.addInventoryIds = addInventoryIds;
    }

    public void setUpdateInventoryIds(List<Long> updateInventoryIds) {
        this.updateInventoryIds = updateInventoryIds;
    }
}
