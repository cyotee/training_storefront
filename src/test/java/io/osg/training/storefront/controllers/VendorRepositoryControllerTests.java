package io.osg.training.storefront.controllers;

import io.osg.training.storefront.model.entities.VendorEntity;
import io.osg.training.storefront.model.entities.VendorEntityTestRig;
import io.osg.training.storefront.respositories.VendorRepository;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("development")
@Sql(scripts = {"classpath:scripts/sql/drop-h2.sql", "classpath:scripts/sql/schema-h2.sql"})
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/snippets")
public class VendorRepositoryControllerTests {

    private static final Logger logger = LoggerFactory.getLogger(VendorRepositoryControllerTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private VendorEntityTestRig vendorEntityTestRig;

    /*@Autowired
    private VendorRepository vendorRepository;*/

    String endPoint;
    String searchEndPoint;
    String path;
    String searchPath;

    @Before
    public void setup(){
        vendorEntityTestRig.initializeDbWithVendors(dataSource, vendorEntityTestRig.getTestTable(), vendorEntityTestRig.getTestColumns(), vendorEntityTestRig.getVendorEntitiesMap());

        endPoint = VendorRepository.class.getAnnotation(RepositoryRestResource.class).path();
        searchEndPoint = endPoint + "/search";
        path = "/" + endPoint;
        searchPath = "/" + searchEndPoint;
        logger.info("Testing path " + path);
    }

    @Test
    public void documentSearchMethods() throws Exception {

        mockMvc.perform(get(path + "/search"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document(endPoint + "/search"));
    }

    @Test
    public void findAllVendorsGetTest() throws Exception {

        mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_embedded.vendors", hasSize(vendorEntityTestRig.getVendorEntitiesMap().size())))
            .andExpect(jsonPath("_embedded.vendors[*].vendorKey", containsInAnyOrder(vendorEntityTestRig.getVendorKeys().toArray(new Integer[vendorEntityTestRig.getVendorNames().size()]))))
            .andExpect(jsonPath("_embedded.vendors[*].vendorName", containsInAnyOrder(vendorEntityTestRig.getVendorNames().toArray(new String[vendorEntityTestRig.getVendorNames().size()]))))
            .andDo(print())
            .andDo(document(endPoint));
    }

    @Test
    public void findVendorByIdGetTest() throws Exception {
        for(VendorEntity vendorEntity : vendorEntityTestRig.getVendorEntitiesMap().values()){
            mockMvc.perform(get(path + "/" + vendorEntity.getVendorKey()))
                    .andExpect(status().isOk())
                    .andExpect( jsonPath("vendorKey", is(vendorEntity.getVendorKey())))
                    .andExpect( jsonPath("vendorName", is(vendorEntity.getVendorName())))
                    .andDo(print())
                    .andDo(document(endPoint + "/" + vendorEntity.getVendorKey()));
        }
    }

    @Test
    public void findByVendorNameContainsIgnoreCaseTest() throws Exception {
        String testEndPoint = searchEndPoint + "/findByVendorNameContainsIgnoreCase";
        //String testPath = "/" + testEndPoint

        for(VendorEntity vendorEntity : vendorEntityTestRig.getVendorEntitiesMap().values()){
            for(String searchCriterion : vendorEntityTestRig.getStringSearchCriteria(vendorEntity.getVendorName())){
                logger.debug("Testing search with criterion: " + searchCriterion);
            }

            for(String testSearchCriteria : vendorEntityTestRig.getStringSearchCriteria(vendorEntity.getVendorName())){
                mockMvc.perform(get(searchPath + "/" + "findByVendorNameContainsIgnoreCase").param("vendorName", testSearchCriteria))
                    .andExpect(jsonPath("_embedded.vendors[*][?(@.vendorKey =~/.*" + vendorEntity.getVendorKey() + ".*/i)].vendorKey", contains(vendorEntity.getVendorKey()) ))
                    .andDo(print())
                    .andDo(document(searchEndPoint + "/" + "findByVendorNameContainsIgnoreCase" + "/" + vendorEntity.getVendorName() + "/" + "parameters" + "/" + testSearchCriteria));
            }
        }
    }
}
