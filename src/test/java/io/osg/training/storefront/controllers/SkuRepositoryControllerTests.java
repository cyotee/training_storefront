package io.osg.training.storefront.controllers;

import io.osg.training.storefront.model.entities.SkuEntity;
import io.osg.training.storefront.model.entities.SkuEntityTestRig;
import io.osg.training.storefront.model.entities.VendorEntity;
import io.osg.training.storefront.respositories.SkuRepository;
import io.osg.training.storefront.respositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("development")
@Sql(scripts = {"classpath:scripts/sql/drop-h2.sql", "classpath:scripts/sql/schema-h2.sql"})
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "build/snippets")
public class SkuRepositoryControllerTests {

    private static final Logger logger = LoggerFactory.getLogger(VendorRepositoryControllerTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private SkuEntityTestRig skuEntityTestRig;

    /*@Autowired
    private VendorRepository vendorRepository;*/

    String endPoint;
    String searchEndPoint;
    String path;
    String searchPath;

    @Before
    public void setup(){
        skuEntityTestRig.initializeDbWithSkus(dataSource, skuEntityTestRig.getTestTable(), skuEntityTestRig.getTestColumns(), skuEntityTestRig.getSkuEntitiesMap());

        endPoint = SkuRepository.class.getAnnotation(RepositoryRestResource.class).path();
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
    public void findAllSkusGetTest() throws Exception {

        mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andExpect(jsonPath("_embedded.skus", hasSize(skuEntityTestRig.getSkuEntitiesMap().size())))
            .andExpect(jsonPath("_embedded.skus[*].skuKey", containsInAnyOrder(skuEntityTestRig.getSkuKeys().toArray(new Integer[skuEntityTestRig.getSkuKeys().size()]))))
            .andExpect(jsonPath("_embedded.skus[*].skuName", containsInAnyOrder(skuEntityTestRig.getSkuNames().toArray(new String[skuEntityTestRig.getSkuNames().size()]))))
            .andExpect(jsonPath("_embedded.skus[*].skuDescription", containsInAnyOrder(skuEntityTestRig.getSkuDescriptions().toArray(new String[skuEntityTestRig.getSkuDescriptions().size()]))))
            .andExpect(jsonPath("_embedded.skus[*].vendorSkuCode", containsInAnyOrder(skuEntityTestRig.getVendorSkuCodes().toArray(new String[skuEntityTestRig.getVendorSkuCodes().size()]))))
            .andDo(print())
            .andDo(document(endPoint));
    }

    @Test
    public void findSkuByIdGetTest() throws Exception {
        for(SkuEntity skuEntity : skuEntityTestRig.getSkuEntitiesMap().values()){
            mockMvc.perform(get(path + "/" + skuEntity.getSkuKey()))
                .andExpect(status().isOk())
                .andExpect( jsonPath("skuKey", is(skuEntity.getSkuKey())))
                .andExpect( jsonPath("vendorSkuCode", is(skuEntity.getVendorSkuCode())))
                .andExpect( jsonPath("skuName", is(skuEntity.getSkuName())))
                .andExpect( jsonPath("skuDescription", is(skuEntity.getSkuDescription())))
                .andExpect( jsonPath("skuVendor.vendorKey", is(skuEntity.getSkuVendor().getVendorKey())))
                .andExpect( jsonPath("skuVendor.vendorName", is(skuEntity.getSkuVendor().getVendorName())))
                .andDo(print())
                .andDo(document(endPoint + "/" + skuEntity.getSkuKey()));
        }
    }

    @Test
    public void findBySkuVendorVendorKeyTest() throws Exception {

        for(SkuEntity skuEntity : skuEntityTestRig.getSkuEntitiesMap().values()){
            List<String> vendorKeys = new ArrayList();
            vendorKeys.add(skuEntity.getSkuVendor().getVendorKey().toString());
            mockMvc.perform(get(searchPath + "/" + "findBySkuVendorVendorKey").param("vendorKey", skuEntity.getSkuVendor().getVendorKey().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.skus", hasSize(2)))
                .andExpect(jsonPath("$..skuVendor[?(@.vendorKey =~/.*" + skuEntity.getSkuVendor().getVendorKey() + ".*/)].vendorKey", containsInAnyOrder(skuEntity.getSkuVendor().getVendorKey())))
                .andDo(print())
                .andDo(document(searchEndPoint + "/" + "findBySkuVendorVendorKey" + "/" + skuEntity.getSkuName() + "/" + "parameters" + "/" + skuEntity.getSkuVendor().getVendorKey()));
        }
    }
}
