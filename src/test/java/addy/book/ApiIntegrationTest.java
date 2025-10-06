package addy.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void fullCrudFlow_withAddressField() {
        ResponseEntity<AddressBook> createResp = rest.postForEntity(url("/api/addressbooks"), null, AddressBook.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        AddressBook ab = createResp.getBody();
        assertThat(ab).isNotNull();
        Long abId = ab.getId();
        assertThat(abId).isNotNull();

        Map<String,String> alice = new HashMap<>();
        alice.put("name","Alice");
        alice.put("phone","555-1111");
        alice.put("address","100 Main St");
        ResponseEntity<AddressBook> addResp1 = rest.postForEntity(url("/api/addressbooks/" + abId + "/buddies"), alice, AddressBook.class);
        assertThat(addResp1.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Map<String,String> bob = new HashMap<>();
        bob.put("name","Bob");
        bob.put("phone","555-2222");
        bob.put("address","200 King Rd");
        ResponseEntity<AddressBook> addResp2 = rest.postForEntity(url("/api/addressbooks/" + abId + "/buddies"), bob, AddressBook.class);
        assertThat(addResp2.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<AddressBook> getResp = rest.getForEntity(url("/api/addressbooks/" + abId), AddressBook.class);
        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        AddressBook loaded = getResp.getBody();
        assertThat(loaded).isNotNull();
        assertThat(loaded.getBuddies()).hasSize(2);
        BuddyInfo first = loaded.getBuddies().get(0);
        assertThat(first.getAddress()).isNotNull();

        ResponseEntity<BuddyInfo[]> listResp = rest.getForEntity(url("/api/buddies?name=Alice"), BuddyInfo[].class);
        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResp.getBody()).isNotEmpty();
        assertThat(listResp.getBody()[0].getName()).isEqualTo("Alice");

        Long buddyIdToDelete = listResp.getBody()[0].getId();
        rest.delete(url("/api/addressbooks/" + abId + "/buddies/" + buddyIdToDelete));

        ResponseEntity<AddressBook> afterDelete = rest.getForEntity(url("/api/addressbooks/" + abId), AddressBook.class);
        assertThat(afterDelete.getBody().getBuddies()).hasSize(1);
    }
}
