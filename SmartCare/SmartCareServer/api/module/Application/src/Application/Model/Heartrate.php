<?php
namespace Application\Model;

class Heartrate extends DoctrineModel
{
    public function getHeartrates(){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array('h.value', 'h.date', 'h.patientid'))
            ->from('Application\Entity\Heartrate', 'h');
        $query = $qb->getQuery();
        $result = $query->getScalarResult();
        return $result;
    }

    public function getHeartratesByPatient($patientId){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array('h.value', 'h.date'))
            ->from('Application\Entity\Heartrate', 'h')
            ->leftJoin('Application\Entity\Patient', 'p', 'WITH', 'p.id = h.patientid')
            ->where($qb->expr()->andX(
                $qb->expr()->eq('p.id', $patientId)
            ));
        $query = $qb->getQuery();
        $result = $query->getScalarResult();
        return $result;
    }
}