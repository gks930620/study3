package com.study.free.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.study.exception.BizNotEffectedException;
import com.study.exception.BizNotFoundException;
import com.study.exception.BizPasswordNotMatchedException;
import com.study.exception.DaoException;
import com.study.free.dao.FreeBoardDaoOracle;
import com.study.free.dao.IFreeBoardDao;
import com.study.free.vo.FreeBoardSearchVO;
import com.study.free.vo.FreeBoardVO;

public class FreeBoardServiceImpl implements IFreeBoardService {
	
	private IFreeBoardDao freeBoardDao = new FreeBoardDaoOracle();


	@Override
	public FreeBoardVO getBoard(int boNo) throws BizNotFoundException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:study");
			FreeBoardVO vo = freeBoardDao.getBoard(conn, boNo);
			if(vo == null) {
				throw new BizNotFoundException("[" + boNo + "] 조회 실패");
			}
			return vo;
		} catch (SQLException e) {
			throw new DaoException("조회시", e);
		} finally {
			if(conn != null) try{ conn.close();} catch(SQLException e) {}
		}
	}

	@Override
	public void registBoard(FreeBoardVO board) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:study");
			freeBoardDao.insertBoard(conn, board);
		} catch (SQLException e) {
			throw new DaoException(e);
		} finally {
			if(conn != null) try{ conn.close();} catch(SQLException e) {}
		}
	}

	@Override
	public void modifyBoard(FreeBoardVO board)
			throws BizNotFoundException, BizPasswordNotMatchedException, BizNotEffectedException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:study");
			FreeBoardVO vo = freeBoardDao.getBoard(conn, board.getBoNo());
			if(vo == null) {
				throw new BizNotFoundException("[" + board.getBoNo() + "] 수정 실패");
			}
			if(!(vo.getBoPass()).equals(board.getBoPass())) {
				throw new BizPasswordNotMatchedException("[" + board.getBoNo() + "] 수정 실패. 비밀번호가 틀립니다.");
			}
			int cnt = freeBoardDao.updateBoard(conn, board);
			if(cnt<1) {
				throw new BizNotEffectedException("[" + board.getBoNo() + "] 수정 실패");
			}
		} catch (SQLException e) {
			throw new DaoException("조회시", e);
		} finally {
			if(conn != null) try{ conn.close();} catch(SQLException e) {}
		}
	}

	@Override
	public void removeBoard(FreeBoardVO board)
			throws BizNotFoundException, BizPasswordNotMatchedException, BizNotEffectedException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:study");
			FreeBoardVO vo = freeBoardDao.getBoard(conn, board.getBoNo());
			if(vo == null) {
				throw new BizNotFoundException("[" + board.getBoNo() + "] 수정 실패");
			}
			if(!(vo.getBoPass()).equals(board.getBoPass())) {
				throw new BizPasswordNotMatchedException("[" + board.getBoNo() + "] 수정 실패");
			}
			int cnt = freeBoardDao.deleteBoard(conn, board);
			if(cnt<1) {
				throw new BizNotEffectedException("[" + board.getBoNo() + "] 수정 실패");
			}
		} catch (SQLException e) {
			throw new DaoException("조회시", e);
		} finally {
			if(conn != null) try{ conn.close();} catch(SQLException e) {}
		}
	}

	@Override
	public void increaseHit(int boNo) throws BizNotEffectedException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:study");
			int cnt = freeBoardDao.increaseHit(conn, boNo);
			if(cnt<1) {
				throw new BizNotEffectedException("조회수 수정 실패");
			}
		} catch (SQLException e) {
			throw new DaoException("조회시", e);
		} finally {
			if(conn != null) try{ conn.close();} catch(SQLException e) {}
		}
	}
	
	@Override
	public List<FreeBoardVO> getBoardList(FreeBoardSearchVO searchVO) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:study");
			// 건수를 구해서 searchVO에 설정하고, searchVO에 pageSetting()을 부르고  list 호출
			int cnt = freeBoardDao.getBoardCount(conn, searchVO);
			searchVO.setTotalRowCount(cnt);
			searchVO.pageSetting();
			
			List<FreeBoardVO> list = freeBoardDao.getBoardList(conn, searchVO);
			return list;
		} catch (SQLException e) {
			throw new DaoException("조회시", e);
		} finally {
			if(conn != null) try{ conn.close();} catch(SQLException e) {}
		}
	}
}
