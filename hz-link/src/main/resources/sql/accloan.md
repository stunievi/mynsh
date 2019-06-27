04
===
select
p1.LOAN_ACCOUNT,
g6.GUARANTY_ID,
g1.GUAR_NO,
g1.GUAR_NAME,
g1.CER_TYPE,
g1.CER_NO,
g6.GAGE_NAME,
p1.cus_id,
case

when p1.ASSURE_MEANS_MAIN = '10' then

g3.CORE_VALUE

when p1.ASSURE_MEANS_MAIN = '20' then

g4.CORE_VALUE

when p1.ASSURE_MEANS_MAIN = '30' then

'0'

end as CORE_VALUE
from
RPT_M_RPT_SLS_ACCT as p1
left join  GRT_LOANGUAR_INFO as g5 on g5.CONT_NO=p1.CONT_NO
left join GRT_GUAR_CONT as g1 on g5.GUAR_CONT_NO=g1.GUAR_CONT_NO
left join GRT_GUARANTY_RE as g6 on g6.GUAR_CONT_NO=g5.GUAR_CONT_NO
left join GRT_G_BASIC_INFO as g3 on g3.GUARANTY_ID=g6.GUARANTY_ID
left join GRT_P_BASIC_INFO as g4 on g4.GUARANTY_ID=g6.GUARANTY_ID
left join GRT_GUARANTEER as g2 on g2.GUARANTY_ID=g6.GUARANTY_ID
where 
1 = 1
    and p1.LOAN_ACCOUNT in (#join(loans)#)
    
    
cun_cus_com
===
select 
p1.CUS_NAME
 from
(select a.*,row_number() over(partition by CUS_NAME order by CUS_NAME) rn from 
(select p.* from RPT_M_RPT_SLS_ACCT p
where 
p.ACCOUNT_STATUS in ('1','6') 
and p.GL_CLASS not like '0%'
and CUST_TYPE like '2%') a) p1 
left join t_dict d on d.name = 'CERT_TYPE' and d.v_key = p1.CERT_TYPE
where rn = 1
@if(has(names)){
    and p1.cus_name in (#join(names)#)
@}
@if(has(certCode)){
    and p1.psn_cert_code = #certCode#
@}

11_5
===
 
SELECT * FROM
(
select cus_name,GUAR_NAME,CER_NO,CER_TYPE,GUAR_WAY,TYPE_CN,a.PSN_CERT_CODE,a.ENT_CERT_CODE from
(select distinct cont_no,CUS_NAME,PSN_CERT_CODE,ENT_CERT_CODE from RPT_M_RPT_SLS_ACCT
where GL_CLASS not like '0%'
and LN_TYPE in ('普通贷款','银团贷款')
and ACCOUNT_STATUS = 1) as a
inner join DAN_BAO_REN as b on a.CONT_NO =b.CONT_NO
)
WHERE CER_NO IN
(SELECT CER_NO FROM
(select distinct guar_name,cer_no from
(select * from
(select a.*,row_number() over(partition by GUAR_NAME,CER_NO order by GUAR_NAME) rn 
from 
(
select * from
(select distinct cont_no,CUS_NAME from RPT_M_RPT_SLS_ACCT
where GL_CLASS not like '0%'
and LN_TYPE in ('普通贷款','银团贷款')
and ACCOUNT_STATUS = 1) as a
inner join DAN_BAO_REN as b on a.CONT_NO =b.CONT_NO
) a
) b 
where rn >1
)
) 
)
order by GUAR_NAME

11_6
===
select a.CUS_NAME,a.CERT_CODE,a.GUAR_NAME,a.CER_NO,p.LOAN_ACCOUNT,p.LOAN_BALANCE from
(
select d.CER_NO,min(d.GUAR_NAME) as GUAR_NAME,min(c.CUS_NAME) as CUS_NAME,min(c.CERT_CODE) as CERT_CODE from
(
select distinct cont_no,CUS_NAME,CERT_TYPE,case PSN_CERT_CODE when '' then ENT_CERT_CODE else PSN_CERT_CODE end as CERT_CODE from RPT_M_RPT_SLS_ACCT
where GL_CLASS not like '0%'
and LN_TYPE in ('普通贷款','银团贷款')
and ACCOUNT_STATUS = 1) as c
inner join DAN_BAO_REN as d on c.CONT_NO =d.CONT_NO and c.CERT_CODE <> d.CER_NO
group by CER_NO
) as a
inner join RPT_M_RPT_SLS_ACCT as p on p.CUS_NAME= a.GUAR_NAME and (a.CER_NO = p.PSN_CERT_CODE or a.CER_NO = p.ENT_CERT_CODE)
and p.GL_CLASS not like '0%'
and p.LN_TYPE in ('普通贷款','银团贷款')
and p.ACCOUNT_STATUS = 1
order by a.GUAR_NAME
