package io.osg.training.storefront.model.entities;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Component
@Scope("prototype")
@Table(name="CLASS_SESSIONS", schema = "TRAINING_STOREFRONT")
public class ClassSessionEntity {

    @Id
    @Column(name="CLASS_SESSION_KEY")
    @NotNull
    private Integer classSessionKey;

    @Autowired
    @ManyToOne
    @JoinColumn(name="SKU_KEY")
    @NotNull
    private SkuEntity classSessionSku;

    @Autowired
    @ManyToOne
    @JoinColumn(name="ADDRESS_KEY")
    @NotNull
    private AddressEntity classSessionAddress;

    @Column(name="CLASS_SESSION_STARTTIME")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime classSessionStartDate;

    @Column(name="CLASS_SESSION_ENDTIME")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime classSessionEndDate;

    @Column(name="CLASS_SESSION_TIMEZONE")
    private String classSessionTimeZone;

    public Integer getClassSessionKey() {
        return  classSessionKey;
    }

    public void setClassSessionKey(Integer classSessionKey) {
        this.classSessionKey = classSessionKey;
    }

    public SkuEntity getClassSessionSku() {
        return classSessionSku;
    }

    public void setClassSessionSku(SkuEntity classSku) {
        this.classSessionSku = classSessionSku;
    }

    public AddressEntity getClassSessionAddress() {
        return classSessionAddress;
    }

    public void setClassSessionAddress(AddressEntity classSessionAddress) { this.classSessionAddress = classSessionAddress; }

    public String getClassSessionStartDate() {
        return classSessionStartDate.toString("MM/dd/yyyy HH:mm:ss");
    }

    public void setClassSessionStartDate(DateTime dateTime) {
        this.classSessionStartDate = classSessionStartDate;
    }

    public String getClassSessionEndDate() {
        return classSessionEndDate.toString("MM/dd/yyyy HH:mm:ss");
    }

    public void setClassSessionEndDate(DateTime dateTime) {
        this.classSessionEndDate = classSessionEndDate;
    }

    public String getClassSessionTimeZone() {
        return classSessionTimeZone;
    }

    public void setClassSessionTimeZone(String classSessionTimeZone) {
        this.classSessionTimeZone = classSessionTimeZone;
    }
}
