package io.osg.training.storefront;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Insert;
import com.ninja_squad.dbsetup.operation.Operation;
import io.osg.training.storefront.model.entities.SkuEntity;
import io.osg.training.storefront.model.entities.VendorEntity;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;

public abstract class AbstractTestRig implements TestRig{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void initializeDbWithControlValues(DataSource dataSourceToInitialize, String tableToInsertTestValuesInto, List<String> columnsToInsertValuesInto, Map<Object, Object> entitiesToInsert){
        Operation initializeWithTestDataOperation = getDbSetupOperation(tableToInsertTestValuesInto, columnsToInsertValuesInto, entitiesToInsert);

        initializeDbWithDbSetup(dataSourceToInitialize, initializeWithTestDataOperation);
    }

    private Operation getDbSetupOperation(String tableToInsertTestValuesInto, List<String> columnsToInsertValuesInto, Map<Object, Object> entitiesToInsert){
        Operation initializeWithTestDataOperation = getDeleteTableOperation(tableToInsertTestValuesInto);

        Insert.Builder insertBuilder = getInsertBuilderForTable(tableToInsertTestValuesInto, columnsToInsertValuesInto);;

        insertBuilder = getInsertBuilderForValuesToInsert(insertBuilder, columnsToInsertValuesInto, entitiesToInsert);

        Insert valuesToInsert = insertBuilder.build();

        return sequenceOf(initializeWithTestDataOperation, valuesToInsert);
    }

    private Operation getDeleteTableOperation(String testTableName){
        return deleteAllFrom(testTableName);
    };

    private Insert.Builder getInsertBuilderForTable(String testTable, List<String> testColumns){
        return insertInto(testTable).columns(testColumns.toArray(new String[testColumns.size()]));
    }

    private Insert.Builder getInsertBuilderForValuesToInsert(Insert.Builder tableToInsertInto, List<String> columnToInsertInto, Map<Object, Object> entitiesMapToInsert){

        for (Object entityToInsert : entitiesMapToInsert.values()){
            Map<String, Object> valuesToInsert = null;
            try {
                valuesToInsert = initailizeTestValuesToInsert(columnToInsertInto, entityToInsert);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tableToInsertInto.values(valuesToInsert);
        }

        return tableToInsertInto;
    }

    protected Map<String, Object> initailizeTestValuesToInsert(List<String> testColumns, Object entityToInsert) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException{ return null;}

    protected String transformSqlColumnNamesToJavaPropertyName(String columnNameToTransform){
        StringBuffer propertyName = new StringBuffer(columnNameToTransform.toLowerCase());

        while(propertyName.toString().contains("_")){
            propertyName = removeUnderScoreCharacter(propertyName);
        }
        return propertyName.toString();
    }

    private StringBuffer removeUnderScoreCharacter(StringBuffer columnNameToTransform){
        StringBuffer propertyName = new StringBuffer(columnNameToTransform);
        Integer indexOfUnderscore = columnNameToTransform.indexOf("_");

        propertyName = propertyName.replace(indexOfUnderscore+1, indexOfUnderscore+2, propertyName.substring(indexOfUnderscore+1, indexOfUnderscore+2).toUpperCase());
        propertyName = propertyName.replace(indexOfUnderscore, indexOfUnderscore+1, "");

        return propertyName;
    }

    private void initializeDbWithDbSetup(DataSource dataSourceToInitialize, Operation dbSetupOperations){
        DataSourceDestination dataSourceDestination = new DataSourceDestination(dataSourceToInitialize);
        DbSetup dbSetup = new DbSetup(dataSourceDestination, dbSetupOperations);
        dbSetup.launch();
    }

    public List<String> getStringSearchCriteria(String searchCriterion){

        List<String> searchCriteria = new ArrayList<String>();
        //String vendorSearchCriterion = vendorEntityToFind.getVendorName();

        searchCriteria.add(searchCriterion);
        searchCriteria.add(searchCriterion.toLowerCase());
        searchCriteria.add(searchCriterion.toUpperCase());

        searchCriteria.addAll(getSearchCriteriaFromStartAscending(searchCriterion));
        searchCriteria.addAll(getSearchCriteriaFromStartAscending(searchCriterion.toLowerCase()));
        searchCriteria.addAll(getSearchCriteriaFromStartAscending(searchCriterion.toUpperCase()));

        searchCriteria.addAll(getSearchCriteriaFromStartDescending(searchCriterion));
        searchCriteria.addAll(getSearchCriteriaFromStartDescending(searchCriterion.toLowerCase()));
        searchCriteria.addAll(getSearchCriteriaFromStartDescending(searchCriterion.toUpperCase()));

        return searchCriteria;
    }

    protected List<String> getSearchCriteriaFromStartAscending(String searchCriterion){
        List<String> searchCriteria = new ArrayList<String>();

        logger.debug("Generating ascending length search criteria from " + searchCriterion + " of length " + searchCriterion.length());

        for (Integer iteration = 0; iteration <= searchCriterion.length(); iteration++){
            logger.debug("Starting iteration: " + iteration);
            logger.debug("Adding search criteria " + searchCriterion.substring(0, iteration));
            String newSearchCriterion = searchCriterion.substring(0, iteration);
            searchCriteria.add(newSearchCriterion);
        }

        return searchCriteria;
    }

    protected List<String> getSearchCriteriaFromStartDescending(String searchCriterion){
        List<String> searchCriteria = new ArrayList<String>();

        logger.debug("Generating descending length search criteria from " + searchCriterion + " of length " + searchCriterion.length());

        for (Integer iteration = 0; iteration <= searchCriterion.length(); iteration++){
            logger.debug("Starting iteration: " + iteration);
            logger.debug("Adding search criteria " + searchCriterion.substring(iteration, searchCriterion.length()));
            String newSearchCriterion = searchCriterion.substring(iteration, searchCriterion.length());
            searchCriteria.add(newSearchCriterion);
        }

        return searchCriteria;
    }
}