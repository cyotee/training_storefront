package io.osg.training.storefront.model.entities;

import io.osg.training.storefront.AbstractTestRig;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.ninja_squad.dbsetup.Operations.*;

@Component
@Lazy
@ConfigurationProperties(prefix="test.rigs.vendorEntitiesRig")
@Profile("development")
public class VendorEntityTestRig extends AbstractTestRig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private VendorEntity newVendor;

    private String newVendorQuery;

    private String testTable;
    private List<String> testColumns;

    private Map<Integer, VendorEntity> vendorEntitiesMap;

    @Autowired
    private VendorEntityRowMapper vendorEntityRowMapper;

    private List<Integer> vendorKeys;
    private List<String> vendorNames;

    @PostConstruct
    public void init() {
        logger.info("Testing table: " + getTestTable());

        for(String testColumn : getTestColumns()){
            logger.info("Testing columns: " + testColumn);
        }

        for(VendorEntity vendorEntityToInitialize : getVendorEntitiesMap().values()){
            logger.info("Using initialized skuEntity: " + vendorEntityToInitialize.toString());
        }

        vendorNames = new ArrayList<String>();
        vendorKeys = new ArrayList<Integer>();

        for(VendorEntity vendor : vendorEntitiesMap.values()){
            vendorKeys.add(vendor.getVendorKey());
            vendorNames.add(vendor.getVendorName());
        }
    }

    public void initializeDbWithVendors(DataSource dataSourceToInitialize, String tableToInsertTestValuesInto, List<String> columnsToInsertValuesInto, Map<Integer, VendorEntity> vendorEntitiesToInsert) {

        logger.info("Testing table: " + getTestTable());

        for(String testColumn : getTestColumns()){
            logger.info("Testing columns: " + testColumn);
        }

        for(VendorEntity vendorEntityToInitialize : getVendorEntitiesMap().values()){
            logger.info("Using initialized skuEntity: " + vendorEntityToInitialize.toString());
        }
        initializeDbWithControlValues(dataSourceToInitialize, tableToInsertTestValuesInto, columnsToInsertValuesInto, new HashMap<Object, Object>(vendorEntitiesToInsert));

    };

    @Override
    protected Map<String, Object> initailizeTestValuesToInsert(List<String> testColumns, Object entityToInsert) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Map<String, Object> valuesToInsert = new HashMap<String, Object>();

        Map<String, String> fieldValues = BeanUtils.describe(entityToInsert);

        for (String testColumnName : testColumns){
            valuesToInsert.put(testColumnName, fieldValues.get(transformSqlColumnNamesToJavaPropertyName(testColumnName)));
        }
        return valuesToInsert;
    }

    public VendorEntity getNewVendor() {
        return newVendor;
    }

    public void setNewVendor(VendorEntity newVendor) {
        this.newVendor = newVendor;
    }

    public String getNewVendorQuery() {
        return newVendorQuery;
    }

    public void setNewVendorQuery(String newVendorQuery) {
        this.newVendorQuery = newVendorQuery;
    }

    public String getTestTable() {
        return testTable;
    }

    public void setTestTable(String testTable) {
        this.testTable = testTable;
    }

    public List<String> getTestColumns() {
        return testColumns;
    }

    public void setTestColumns(List<String> testColumns) {
        this.testColumns = testColumns;
    }

    public Map<Integer, VendorEntity> getVendorEntitiesMap() {
        return vendorEntitiesMap;
    }

    public void setVendorEntitiesMap(Map<Integer, VendorEntity> vendorEntitiesMap) {
        this.vendorEntitiesMap = vendorEntitiesMap;
    }

    public VendorEntityRowMapper getVendorEntityRowMapper() {
        return vendorEntityRowMapper;
    }

    public List<Integer> getVendorKeys() {
        return vendorKeys;
    }

    public void setVendorKeys(List<Integer> vendorKeys) {
        this.vendorKeys = vendorKeys;
    }

    public void setVendorEntityRowMapper(VendorEntityRowMapper vendorEntityRowMapper) {
        this.vendorEntityRowMapper = vendorEntityRowMapper;
    }

    public List<String> getVendorNames() {
        return vendorNames;
    }

    public void setVendorNames(List<String> vendorNames) {
        this.vendorNames = vendorNames;
    }

    @Component
    @Lazy
    public class VendorEntityRowMapper implements RowMapper<VendorEntity> {

        @Autowired
        private ApplicationContext context;

        @Override
        public VendorEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            VendorEntity vendorEntity = context.getBean(VendorEntity.class);

            Iterator<String> testColumnsIterator = getTestColumns().iterator();

            vendorEntity.setVendorKey(rs.getInt(testColumnsIterator.next()));
            vendorEntity.setVendorName(rs.getString(testColumnsIterator.next()));

            return vendorEntity;
        }
    }
}
