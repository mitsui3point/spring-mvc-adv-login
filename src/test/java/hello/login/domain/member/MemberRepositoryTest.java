package hello.login.domain.member;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberRepositoryTest {

    private MemberRepository memberRepository = new MemberRepository();

    @Test
    void saveTest() {
        //given
        Member member = new Member("loginId1", "name", "password");
        //when
        Member savedMember = memberRepository.save(member);
        //then
        assertThat(savedMember).extracting("id").isNotNull();
        assertThat(savedMember).extracting("loginId").isEqualTo(member.getLoginId());
        assertThat(savedMember).extracting("name").isEqualTo(member.getName());
        assertThat(savedMember).extracting("password").isEqualTo(member.getPassword());
    }

    @Test
    void findAllTest() {
        //given
        Member member1 = new Member("loginId1", "name1", "password");
        Member member2 = new Member("loginId2", "name2", "password");
        List<Member> savedMembers = Arrays.asList(memberRepository.save(member1), memberRepository.save(member2));

        //when
        List<Member> members = memberRepository.findAll();

        //then
        assertThat(members).isEqualTo(savedMembers);
    }

    @Test
    void findByIdTest() {
        //given
        Member findTargetMember = memberRepository.save(new Member("loginId1", "name1", "password"));
        memberRepository.save(new Member("loginId2", "name2", "password"));

        //when
        Member findMember = memberRepository.findById(findTargetMember.getId());

        //then
        assertThat(findMember).isEqualTo(findTargetMember);
    }

    @Test
    void findByLoginIdTest() {
        //given
        Member member1 = new Member("loginId1", "name1", "password");
        Member member2 = new Member("loginId2", "name2", "password");
        Optional<Member> loginMemberResult = Optional.of(memberRepository.save(member1));
        memberRepository.save(member2);

        //when
        Optional<Member> loginMember = memberRepository.findByLoginId(member1.getLoginId());

        //then
        assertThat(loginMember).isEqualTo(loginMemberResult);
    }

    @Test
    void findByLoginIdEmptyTest() {
        //given
        Member member1 = new Member("loginId1", "name1", "password");
        Member member2 = new Member("loginId2", "name2", "password");
        memberRepository.save(member1);
        memberRepository.save(member2);
        Optional<Member> loginMemberResult = Optional.empty();

        //when
        Optional<Member> loginMember = memberRepository.findByLoginId("notExistLoginId");

        //then
        assertThat(loginMember).isEqualTo(loginMemberResult);
    }

    @Test
    void clearStoreTest() {
        //given
        Member member1 = new Member("loginId1", "name1", "password");
        Member member2 = new Member("loginId2", "name2", "password");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        memberRepository.clearStore();
        List<Member> result = memberRepository.findAll();

        //then
        assertThat(result).isEmpty();
    }

    @AfterEach
    void tearDown() {
        memberRepository.clearStore();
    }
}
