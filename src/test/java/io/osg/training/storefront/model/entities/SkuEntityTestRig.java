package io.osg.training.storefront.model.entities;

import io.osg.training.storefront.AbstractTestRig;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Lazy
@ConfigurationProperties(prefix="test.rigs.skuEntitiesRig")
@Profile("development")
public class SkuEntityTestRig extends AbstractTestRig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private SkuEntity newSku;

    private String newSkuQuery;

    private String testTable;
    private List<String> testColumns;

    private Map<Integer, SkuEntity> skuEntitiesMap;

    @Autowired
    private VendorEntityTestRig vendorEntityTestRig;

    @Autowired
    private SkuEntityRowMapper SkuEntityRowMapper;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    private List<Integer> skuKeys;
    private List<String> vendorSkuCodes;
    private List<String> skuNames;
    private List<String> skuDescriptions;

    @PostConstruct
    public void init(){
        for(SkuEntity skuEntityToInitialize : skuEntitiesMap.values()){
            logger.info("Initializing skuEntity: " + skuEntityToInitialize.toString());
            skuEntityToInitialize.setSkuVendor(vendorEntityTestRig.getVendorEntitiesMap().get(skuEntityToInitialize.getSkuVendor().getVendorKey()));
            logger.info("Initialized skuEntity: " + skuEntityToInitialize.toString());
        }

        newSku.setSkuVendor(vendorEntityTestRig.getVendorEntitiesMap().get(newSku.getSkuVendor().getVendorKey()));
        logger.info("Using SKU: " + newSku + " as new test SKU.");

        logger.info("Testing table: " + getTestTable());

        for(String testColumn : getTestColumns()){
            logger.info("Testing columns: " + testColumn);
        }

        for(SkuEntity skuEntityToInitialize : getSkuEntitiesMap().values()){
            logger.info("Using initialized skuEntity: " + skuEntityToInitialize.toString());
        }

        skuKeys = new ArrayList<Integer>();
        vendorSkuCodes = new ArrayList<String>();
        skuNames = new ArrayList<String>();
        skuDescriptions = new ArrayList<String>();

        for(SkuEntity sku : skuEntitiesMap.values()){
            skuKeys.add(sku.getSkuKey());
            vendorSkuCodes.add(sku.getVendorSkuCode());
            skuNames.add(sku.getSkuName());
            skuDescriptions.add(sku.getSkuDescription());
        }
    }

    public void initializeDbWithSkus(DataSource dataSourceToInitialize, String tableToInsertTestValuesInto, List<String> columnsToInsertValuesInto, Map<Integer, SkuEntity> skuEntitiesToInsert) {

        logger.info("Starting method: initializeDbWithSkus");
        logger.info("Initialize database with DataSource: " + dataSourceToInitialize.toString());
        logger.info("Testing table: " + tableToInsertTestValuesInto);

        for(String testColumn : columnsToInsertValuesInto){
            logger.info("Testing columns: " + testColumn);
        }

        for(SkuEntity skuEntityToInitialize : skuEntitiesToInsert.values()){
            logger.info("Using initialized skuEntity: " + skuEntityToInitialize.toString());
        }

        vendorEntityTestRig.initializeDbWithVendors(dataSource, vendorEntityTestRig.getTestTable(), vendorEntityTestRig.getTestColumns(), vendorEntityTestRig.getVendorEntitiesMap());
        initializeDbWithControlValues(dataSourceToInitialize, tableToInsertTestValuesInto, columnsToInsertValuesInto, new HashMap<Object, Object>(skuEntitiesToInsert));

    };

    @Override
    protected Map<String, Object> initailizeTestValuesToInsert(List<String> testColumns, Object entityToInsert) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        logger.info("Starting method: initailizeTestValuesToInsert");

        for(String testColumn : testColumns){
            logger.info("Testing columns: " + testColumn);
        }

        Map<String, Object> valuesToInsert = new HashMap<String, Object>();

        Map<String, String> fieldValues = BeanUtils.describe(entityToInsert);

        for(Map.Entry<String, String> entry : fieldValues.entrySet()){
            logger.info("Preparing entry set entry of: " + entry.getKey() + " : " + entry.getValue());
        }

        for (String testColumnName : testColumns){
            String propertyName = transformSqlColumnNamesToJavaPropertyName(testColumnName);
            String fieldValue = fieldValues.get(propertyName);
            logger.info("Setting field value: " + fieldValue + " from field: " + propertyName + " for column: " + testColumnName);
            if( fieldValues.get(propertyName) != null) {
                valuesToInsert.put(testColumnName, fieldValues.get(propertyName));
            }else{
                valuesToInsert.put(testColumnName, ((SkuEntity) entityToInsert).getSkuVendor().getVendorKey());
            }
        }
        return valuesToInsert;
    }

    public Logger getLogger() {
        return logger;
    }

    public SkuEntity getNewSku() {
        return newSku;
    }

    public void setNewSku(SkuEntity newSku) {
        this.newSku = newSku;
    }

    public String getNewSkuQuery() {
        return newSkuQuery;
    }

    public void setNewSkuQuery(String newSkuQuery) {
        this.newSkuQuery = newSkuQuery;
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

    public Map<Integer, SkuEntity> getSkuEntitiesMap() {
        return skuEntitiesMap;
    }

    public void setSkuEntitiesMap(Map<Integer, SkuEntity> skuEntitiesMap) {
        this.skuEntitiesMap = skuEntitiesMap;
    }

    public SkuEntityTestRig.SkuEntityRowMapper getSkuEntityRowMapper() {
        return SkuEntityRowMapper;
    }

    public void setSkuEntityRowMapper(SkuEntityTestRig.SkuEntityRowMapper skuEntityRowMapper) {
        SkuEntityRowMapper = skuEntityRowMapper;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public VendorEntityTestRig getVendorEntityTestRig() {
        return vendorEntityTestRig;
    }

    public void setVendorEntityTestRig(VendorEntityTestRig vendorEntityTestRig) {
        this.vendorEntityTestRig = vendorEntityTestRig;
    }

    public List<Integer> getSkuKeys() {
        return skuKeys;
    }

    public void setSkuKeys(List<Integer> skuKeys) {
        this.skuKeys = skuKeys;
    }

    public List<String> getVendorSkuCodes() {
        return vendorSkuCodes;
    }

    public void setVendorSkuCodes(List<String> vendorSkuCodes) {
        this.vendorSkuCodes = vendorSkuCodes;
    }

    public List<String> getSkuNames() {
        return skuNames;
    }

    public void setSkuNames(List<String> skuNames) {
        this.skuNames = skuNames;
    }

    public List<String> getSkuDescriptions() {
        return skuDescriptions;
    }

    public void setSkuDescriptions(List<String> skuDescriptions) {
        this.skuDescriptions = skuDescriptions;
    }

    @Component
    @Lazy
    public class SkuEntityRowMapper implements RowMapper<SkuEntity> {

        @Autowired
        private ApplicationContext context;

        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Override
        public SkuEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            SkuEntity skuEntity = context.getBean(SkuEntity.class);

            Iterator<String> testColumnsIterator = getTestColumns().iterator();

            skuEntity.setSkuKey(rs.getInt(testColumnsIterator.next()));
            skuEntity.setSkuVendor(jdbcTemplate.queryForObject(vendorEntityTestRig.getNewVendorQuery(), vendorEntityTestRig.getVendorEntityRowMapper(), rs.getInt(testColumnsIterator.next())));
            skuEntity.setVendorSkuCode(rs.getString(testColumnsIterator.next()));
            skuEntity.setSkuName(rs.getString(testColumnsIterator.next()));
            skuEntity.setSkuDescription(rs.getString(testColumnsIterator.next()));

            return skuEntity;
        }
    }
}
