package com.study.member.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.study.exception.BizDuplicateKeyException;
import com.study.exception.BizNotEffectedException;
import com.study.exception.BizNotFoundException;
import com.study.exception.BizPasswordNotMatchedException;
import com.study.exception.DaoDuplicateKeyException;
import com.study.exception.DaoException;
import com.study.free.vo.FreeBoardVO;
import com.study.member.dao.IMemberDao;
import com.study.member.dao.MemberDaoOracle;
import com.study.member.vo.MemberSearchVO;
import com.study.member.vo.MemberVO;

public class MemberServiceImpl implements IMemberService {
	
	private IMemberDao memberDao = new MemberDaoOracle();

	@Override
	public void registMember(MemberVO member) throws BizDuplicateKeyException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:study");
//			MemberVO vo = memberDao.getMember(conn, member.getMemId());
//			if(vo != null) {
//				throw new BizDuplicateKeyException("[" + member.getMemId() + "] 이미 존재합니다.");
//			}
			memberDao.insertMember(conn, member);
		} catch (SQLException e) {
			throw new DaoException(e);
		} catch (DaoDuplicateKeyException e) {
			throw new BizDuplicateKeyException(e.getMessage(),e);
		} finally {
			if(conn != null) try{ conn.close();} catch(SQLException e) {}
		}
	}

	@Override
	public void modifyMember(MemberVO member) throws BizNotEffectedException, BizNotFoundException {
		Connection conn = null;
		try {
			
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:study");
			MemberVO vo = memberDao.getMember(conn, member.getMemId());
			if(vo == null) {
				throw new BizNotFoundException("[" + member.getMemId() + "] 조회 실패");
			}
			int cnt = memberDao.updateMember(conn, member);
			if(cnt<1) {
				throw new BizNotEffectedException("[" + member.getMemId() + "] 수정 실패");
			}
		} catch (SQLException e) {
			throw new DaoException("조회시", e);
		} finally {
			if(conn != null) try{ conn.close();} catch(SQLException e) {}
		}
	}

	@Override
	public void removeMember(MemberVO member) throws BizNotEffectedException, BizNotFoundException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:study");
			MemberVO vo = memberDao.getMember(conn, member.getMemId());
			if(vo == null) {
				throw new BizNotFoundException("[" + member.getMemId() + "] 삭제 실패");
			}
			int cnt = memberDao.deleteMember(conn, member);
			if(cnt<1) {
				throw new BizNotEffectedException("[" + member.getMemId() + "] 삭제 실패");
			}
		} catch (SQLException e) {
			throw new DaoException("조회시", e);
		} finally {
			if(conn != null) try{ conn.close();} catch(SQLException e) {}
		}
	}

	@Override
	public MemberVO getMember(String memId) throws BizNotFoundException {
		Connection conn = null;
		try {
			
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:study");
			MemberVO vo = memberDao.getMember(conn, memId);
			if(vo == null) {
				throw new BizNotFoundException("[" + memId + "] 조회 실패");
			}
			return vo;
		} catch (SQLException e) {
			throw new DaoException("조회시", e);
		} finally {
			if(conn != null) try{ conn.close();} catch(SQLException e) {}
		}
	}

	@Override
	public List<MemberVO> getMemberList(MemberSearchVO searchVO) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:study");
			// 건수를 구해서 searchVO에 설정하고, searchVO에 pageSetting()을 부르고  list 호출
			int cnt = memberDao.getMemberCount(conn, searchVO);
			searchVO.setTotalRowCount(cnt);
			searchVO.pageSetting();
						
			List<MemberVO> list = memberDao.getMemberList(conn, searchVO);
			return list;
		} catch (SQLException e) {
			throw new DaoException("조회시", e);
		} finally {
			if(conn != null) try{ conn.close();} catch(SQLException e) {}
		}
	}
}
