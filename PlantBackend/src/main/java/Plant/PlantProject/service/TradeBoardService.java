package Plant.PlantProject.service;

import Plant.PlantProject.Entity.Status;
import Plant.PlantProject.Entity.TradeBoard;
import Plant.PlantProject.exception.TradeBoardNotFoundException;
import Plant.PlantProject.exception.UserNotFoundException;
import Plant.PlantProject.dto.TradeBoardDto;
import Plant.PlantProject.dto.vo.TradeBoardRequestDto;
import Plant.PlantProject.dto.vo.ResponseTradeBoardDto;
import Plant.PlantProject.messagequeue.KafkaProducer;
import Plant.PlantProject.repository.GoodsRepository;
import Plant.PlantProject.repository.MemberRepository;
import Plant.PlantProject.repository.TradeBoardRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TradeBoardService {
    private final TradeBoardRepository tradeBoardRepository;
    private final MemberRepository memberRepository;
    private final GoodsRepository goodsRepository;
    private final KafkaProducer kafkaProducer;
    // 트랜잭션은 readOnly true 로 설정하면 데이터베이스의 상태를 변경하지 않는 읽기 전용 메서드에서 성능 향상을 기대할 수 있음
    // 트랜잭션 설정을 하면 롤백 가능, 즉 DB에서 무언가 잘못되었을 경우 이전 상태로 되돌릴 수 있음
    @Transactional
    public TradeBoardDto saveTradePost(TradeBoardDto tradeBoardDto){

        TradeBoard savedEntity=tradeBoardRepository.save(tradeBoardDto.toEntity());
        System.out.println("service 통과");
        tradeBoardDto.setId(savedEntity.getId());
        return tradeBoardDto;
    }
    @Transactional
    public ResponseTradeBoardDto saveTradePost(TradeBoardRequestDto tradeBoardDto){
        TradeBoard tradeBoard=tradeBoardRepository.save(
                TradeBoard.createTradeBoard(memberRepository.findById(tradeBoardDto.getMemberId()).orElseThrow(UserNotFoundException::new),
                        tradeBoardDto.getTitle(),
                        tradeBoardDto.getContent(),
                        tradeBoardDto.getCreateBy(),
                        tradeBoardDto.getView(),
                        tradeBoardDto.getPrice(),
                        tradeBoardDto.getGoodCount(),
                        tradeBoardDto.getBuyer()
                        )
        );

        return ResponseTradeBoardDto.convertTradeBoardToDto(tradeBoard);
    }
    @Transactional
    public TradeBoardDto updateTradePost(TradeBoardDto tradeBoardDto) {
        Optional<TradeBoard> optionalTradeBoard = tradeBoardRepository.findById(tradeBoardDto.getId());


        if (optionalTradeBoard.isPresent()) {
            TradeBoard tradeBoard = optionalTradeBoard.get();
            tradeBoard.setTitle(tradeBoardDto.getTitle());
            tradeBoard.setContent(tradeBoardDto.getContent());
            tradeBoard.setStatus(tradeBoardDto.getStatus());
            tradeBoard.setPrice(tradeBoardDto.getPrice());
            TradeBoard savedEntity = tradeBoardRepository.save(tradeBoard);

            // 업데이트된 정보를 TradeBoardDto로 변환하여 반환
            TradeBoardDto updatedTradeBoardDto = new TradeBoardDto();
            updatedTradeBoardDto.setId(savedEntity.getId());
            updatedTradeBoardDto.setTitle(savedEntity.getTitle());
            updatedTradeBoardDto.setContent(savedEntity.getContent());
            // 이 외에 필요한 필드들도 updatedTradeBoardDto에 추가

            return updatedTradeBoardDto;
        } else {
            // 해당 id에 해당하는 게시글이 없는 경우 처리
            throw new EntityNotFoundException("TradeBoard not found with id: " + tradeBoardDto.getId());
        }
    }
    @Transactional
    public TradeBoardDto updateStatus(Long id) {
        Optional<TradeBoard> optionalTradeBoard = tradeBoardRepository.findById(id);

        if (optionalTradeBoard.isPresent()) {
            TradeBoard tradeBoard = optionalTradeBoard.get();
            tradeBoard.setStatus(Status.거래완료);
            TradeBoard savedEntity = tradeBoardRepository.save(tradeBoard);

            // 업데이트된 정보를 TradeBoardDto로 변환하여 반환
            TradeBoardDto updatedTradeBoardDto = new TradeBoardDto();
            updatedTradeBoardDto.setId(savedEntity.getId());
            updatedTradeBoardDto.setTitle(savedEntity.getTitle());
            updatedTradeBoardDto.setContent(savedEntity.getContent());
            updatedTradeBoardDto.setStatus(savedEntity.getStatus());
            // 이 외에 필요한 필드들도 updatedTradeBoardDto에 추가
            return updatedTradeBoardDto;
        } else {
            // 해당 id에 해당하는 게시글이 없는 경우 처리
            throw new EntityNotFoundException("TradeBoard not found with id: " + id);
        }
    }
    @Transactional
    public int updateView(Long id) {
        TradeBoardDto tradeBoardDto = new TradeBoardDto();
        int view = tradeBoardRepository.updateView(id);
        return view;

    }

    @Transactional
    public ResponseTradeBoardDto setBuyer(Long id, TradeBoardRequestDto tradeBoardRequestDto) {
        TradeBoard tradeBoard = tradeBoardRepository.findTradeBoardById(id);
        tradeBoardRepository.updateBuyer(tradeBoard.getId(), tradeBoardRequestDto.getBuyer());
        return ResponseTradeBoardDto.convertTradeBoardToDto(tradeBoard);

    }
    @Transactional
    public Page<ResponseTradeBoardDto> pageList(String search, Pageable pageable) {
        Page<TradeBoard> tradeBoards;

//        if (search != null && !search.trim().isEmpty()) {
            tradeBoards = tradeBoardRepository.findByTitleContainingOrContentContaining(search, search, pageable);
//        } else {
//            tradeBoards = tradeBoardRepository.findAll(pageable);
//        }
        return tradeBoards.map(tradeBoard -> ResponseTradeBoardDto.convertTradeBoardToDto(tradeBoard));
//
//        return tradeBoards.map(tradeBoard -> new TradeBoardDto(tradeBoard.getId(), tradeBoard.getCreateBy(), tradeBoard.getMember(), tradeBoard.getTitle(),
//                tradeBoard.getContent(), tradeBoard.getStatus(), tradeBoard.getCreatedAt(), tradeBoard.getUpdatedAt(), tradeBoard.getView()));
    }
    public TradeBoardDto findById(Long id){
        return tradeBoardRepository.findById(id).map(tradeBoard -> new TradeBoardDto(tradeBoard.getId(), tradeBoard.getCreateBy(),tradeBoard.getMember(),tradeBoard.getTitle(),
                tradeBoard.getContent(),tradeBoard.getStatus(), tradeBoard.getCreatedAt(),tradeBoard.getUpdatedAt(), tradeBoard.getView())).get();
    }
    public ResponseTradeBoardDto findByIdx(Long id){
        TradeBoard tradeBoard = tradeBoardRepository.findById(id).orElseThrow(UserNotFoundException::new);
        ResponseTradeBoardDto responseTradeBoardDto = ResponseTradeBoardDto.convertTradeBoardToDto(tradeBoard);
        return responseTradeBoardDto;
    }
    public synchronized void increaseGoodCount(Long tradeBoardId) {
        TradeBoard tradeBoard = tradeBoardRepository.findById(tradeBoardId)
                .orElseThrow(() -> new TradeBoardNotFoundException("TradeBoard not found"));

        tradeBoard.increaseGoodsCount(); // TradeBoard 엔티티의 메서드를 호출하여 찜 개수 증가
        tradeBoardRepository.save(tradeBoard);
    }
    @Transactional
    public synchronized void decreaseGoodCount(Long tradeBoardId) {
        TradeBoard tradeBoard = tradeBoardRepository.findById(tradeBoardId)
                .orElseThrow(() -> new TradeBoardNotFoundException("TradeBoard not found"));

        tradeBoard.decreaseGoodsCount(); // TradeBoard 엔티티의 메서드를 호출하여 찜 개수 증가
        tradeBoardRepository.save(tradeBoard);
    }


    public void deletePost(TradeBoardDto tradeBoardDto) {

        tradeBoardRepository.delete(tradeBoardDto.toEntity());
        /*send this deletePost to the kafka*/
        kafkaProducer.send("deletePost", tradeBoardDto);
    }

    public ResponseTradeBoardDto findTradeBoardById(Long tradeBoardId) {
        TradeBoard tradeBoard = tradeBoardRepository.findTradeBoardById(tradeBoardId);
        ResponseTradeBoardDto tradeBoardDto = ResponseTradeBoardDto.convertTradeBoardToDto(tradeBoard);
        return tradeBoardDto;
    }
}
