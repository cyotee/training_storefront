package io.osg.training.storefront.model.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Component
@Scope("prototype")
@Table(name="SKUS", schema = "TRAINING_STOREFRONT")
public class SkuEntity {

    @Id
    @Column(name="SKU_KEY")
    @NotNull
    private Integer skuKey;

    @Column(name="VENDOR_SKU_CODE")
    @NotNull
    private String vendorSkuCode;

    @Column(name="SKU_NAME")
    @NotNull
    private String skuName;

    @Column(name="SKU_DESCRIPTION")
    @NotNull
    private String skuDescription;

    @Autowired
    @ManyToOne
    @JoinColumn(name="VENDOR_KEY")
    @NotNull
    private VendorEntity skuVendor;

    public Integer getSkuKey() {
        return skuKey;
    }

    public void setSkuKey(Integer skuKey) {
        this.skuKey = skuKey;
    }

    public String getVendorSkuCode() {
        return vendorSkuCode;
    }

    public void setVendorSkuCode(String vendorSkuCode) {
        this.vendorSkuCode = vendorSkuCode;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSkuDescription() {
        return skuDescription;
    }

    public void setSkuDescription(String skuDescription) {
        this.skuDescription = skuDescription;
    }

    public VendorEntity getSkuVendor() {
        return skuVendor;
    }

    public void setSkuVendor(VendorEntity skuVendor) {
        this.skuVendor = skuVendor;
    }

    @Override
    public String toString() {
        return "SkuEntity{" +
                "skuKey=" + skuKey +
                ", vendorSkuCode='" + vendorSkuCode + '\'' +
                ", skuName='" + skuName + '\'' +
                ", skuDescription='" + skuDescription + '\'' +
                ", skuVendor=" + skuVendor.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkuEntity)) return false;

        SkuEntity skuEntity = (SkuEntity) o;

        if (!getSkuKey().equals(skuEntity.getSkuKey())) return false;
        if (!getVendorSkuCode().equals(skuEntity.getVendorSkuCode())) return false;
        if (!getSkuName().equals(skuEntity.getSkuName())) return false;
        if (!getSkuDescription().equals(skuEntity.getSkuDescription())) return false;
        return getSkuVendor().equals(skuEntity.getSkuVendor());
    }

    @Override
    public int hashCode() {
        int result = getSkuKey().hashCode();
        result = 31 * result + getVendorSkuCode().hashCode();
        result = 31 * result + getSkuName().hashCode();
        result = 31 * result + getSkuDescription().hashCode();
        //result = 31 * result + getSkuVendor().hashCode();
        return result;
    }
}
