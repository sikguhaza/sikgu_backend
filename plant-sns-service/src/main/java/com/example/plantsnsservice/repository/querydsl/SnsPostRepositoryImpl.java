package com.example.plantsnsservice.repository.querydsl;


import com.example.plantsnsservice.dto.response.SnsPostResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.example.plantsnsservice.domain.entity.QHashTag.hashTag;
import static com.example.plantsnsservice.domain.entity.QImage.image;
import static com.example.plantsnsservice.domain.entity.QSnsHashTagMap.snsHashTagMap;
import static com.example.plantsnsservice.domain.entity.QSnsPost.snsPost;
import static com.example.plantsnsservice.dto.SearchParam.*;
import static com.querydsl.core.types.Projections.list;

@RequiredArgsConstructor
public class SnsPostRepositoryImpl implements CustomSnsPostRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public SnsPostResponseDto getSnsPostById(Long snsPostId) {

        return jpaQueryFactory.select(Projections.constructor(SnsPostResponseDto.class,
                        snsPost.id,
                        snsPost.memberNo,
                        snsPost.snsPostTitle,
                        snsPost.snsPostContent,
                        snsPost.createdBy,
                        snsPost.createdAt,
                        snsPost.snsLikesCount,
                        snsPost.snsViewsCount,
                        list(hashTag.name),
                        list(image.url)))
                .from(snsHashTagMap)
                .join(snsHashTagMap.snsPost, snsPost)
                .join(snsHashTagMap.hashTag, hashTag)
                .join(snsPost.imageList, image)
                .where(snsPost.id.eq(snsPostId))
                .fetchOne();
    }
    @Override
    public List<SnsPostResponseDto> findAllByOrderByCreatedAtDesc() {
        return jpaQueryFactory.select(Projections.constructor(SnsPostResponseDto.class,
                        snsPost.id,
                        snsPost.memberNo,
                        snsPost.snsPostTitle,
                        snsPost.snsPostContent,
                        snsPost.createdBy,
                        snsPost.createdAt,
                        snsPost.snsLikesCount,
                        snsPost.snsViewsCount,
                        list(hashTag.name),
                        list(image.url)))
                .from(snsHashTagMap)
                .join(snsHashTagMap.snsPost, snsPost)
                .join(snsHashTagMap.hashTag, hashTag)
                .join(snsPost.imageList, image)
                .orderBy(snsPost.id.desc())
                .fetch();
    }
    @Override
    public List<SnsPostResponseDto> findAllByCreatedBy(String createdBy) {
        return jpaQueryFactory.select(Projections.constructor(SnsPostResponseDto.class,
                        snsPost.id,
                        snsPost.memberNo,
                        snsPost.snsPostTitle,
                        snsPost.snsPostContent,
                        snsPost.createdBy,
                        snsPost.createdAt,
                        snsPost.snsLikesCount,
                        snsPost.snsViewsCount,
                        list(hashTag.name),
                        list(image.url)))
                .from(snsHashTagMap)
                .join(snsHashTagMap.snsPost, snsPost)
                .join(snsHashTagMap.hashTag, hashTag)
                .join(snsPost.imageList, image)
                .where(snsPost.createdBy.eq(createdBy))
                .orderBy(snsPost.createdAt.desc())
                .fetch();
    }
    //이주의 게시글 ==> 조회수 기준
    //만약 조회수가 같으면 좋아요 수 높은 기준
    @Override
    public List<SnsPostResponseDto> findTopPostsByWeek() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        return jpaQueryFactory
                .select(Projections.constructor(SnsPostResponseDto.class,
                        snsPost.id,
                        snsPost.memberNo,
                        snsPost.snsPostTitle,
                        snsPost.snsPostContent,
                        snsPost.createdBy,
                        snsPost.createdAt,
                        snsPost.snsLikesCount,
                        snsPost.snsViewsCount,
                        list(hashTag.name),
                        list(image.url)))
                .from(snsHashTagMap)
                .join(snsHashTagMap.snsPost, snsPost)
                .join(snsHashTagMap.hashTag, hashTag)
                .join(snsPost.imageList, image)
                .where(snsPost.createdAt.after(oneWeekAgo))
                .orderBy(snsPost.snsViewsCount.desc(), snsPost.snsLikesCount.desc())
                .limit(10)
                .fetch();
    }

    //이달의 게시글 ==> 조회수 기준
    //만약 조회수가 같으면 좋아요 수 높은 기준
    @Override
    public List<SnsPostResponseDto> findTopPostsByMonth() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        return jpaQueryFactory
                .select(Projections.constructor(SnsPostResponseDto.class,
                        snsPost.id,
                        snsPost.memberNo,
                        snsPost.snsPostTitle,
                        snsPost.snsPostContent,
                        snsPost.createdBy,
                        snsPost.createdAt,
                        snsPost.snsLikesCount,
                        snsPost.snsViewsCount,
                        list(hashTag.name),
                        list(image.url)))
                .from(snsHashTagMap)
                .join(snsHashTagMap.snsPost, snsPost)
                .join(snsHashTagMap.hashTag, hashTag)
                .join(snsPost.imageList, image)
                .where(snsPost.createdAt.after(oneMonthAgo))
                .orderBy(snsPost.snsViewsCount.desc(), snsPost.snsLikesCount.desc())
                .limit(20)
                .fetch();
    }

    // projection 사용해서 한방 쿼리
    @Override
    public List<SnsPostResponseDto> search(final Map<String, String> searchCondition) {
        return jpaQueryFactory
                .select(Projections.constructor(SnsPostResponseDto.class,
                        snsPost.id,
                        snsPost.memberNo,
                        snsPost.snsPostTitle,
                        snsPost.snsPostContent,
                        snsPost.createdBy,
                        snsPost.createdAt,
                        snsPost.snsLikesCount,
                        snsPost.snsViewsCount,
                        list(hashTag.name),
                        list(image.url)))
                .from(snsHashTagMap)
                .join(snsHashTagMap.snsPost, snsPost)
                .join(snsHashTagMap.hashTag, hashTag)
                .join(snsPost.imageList, image)
                .where(allCond(searchCondition))
                .fetch();
    }

    // BooleanBuilder 검색 동적 쿼리
    private BooleanBuilder allCond(Map<String, String> searchCondition) {
        BooleanBuilder builder = new BooleanBuilder();

        return builder
                .and(snsPostTileLike(searchCondition.getOrDefault(SNSPOSTTITLE.getParamKey(), null)))
                .and(hashTagNameEq(searchCondition.getOrDefault(HASHTAGNAME.getParamKey(), null)))
                .and(snsPostContentLike(searchCondition.getOrDefault(SNSPOSTCONTENT.getParamKey(), null)))
                .and(nicknameEq(searchCondition.getOrDefault(NICKNAME.getParamKey(), null)));
    }
    // 검색 동적 쿼리 조건1
    private BooleanExpression snsPostTileLike(String snsPostTitle) {
        return StringUtils.hasText(snsPostTitle) ? snsPost.snsPostTitle.contains(snsPostTitle) : null;
    }
    // 검색 동적 쿼리 조건2
    private BooleanExpression hashTagNameEq(String hashTagName) {
        return StringUtils.hasText(hashTagName) ? snsHashTagMap.hashTag.name.eq(hashTagName) : null;
    }
    // 검색 동적 쿼리 조건3
    private BooleanExpression snsPostContentLike(String snsPostContent) {
        return StringUtils.hasText(snsPostContent) ? snsPost.snsPostContent.contains(snsPostContent) : null;
    }
    // 검색 동적 쿼리 조건4
    private BooleanExpression nicknameEq(String nickname) {
        return StringUtils.hasText(nickname) ? snsPost.createdBy.eq(nickname) : null;
    }


}