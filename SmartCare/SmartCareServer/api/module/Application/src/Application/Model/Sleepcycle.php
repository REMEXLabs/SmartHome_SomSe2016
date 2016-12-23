<?php
namespace Application\Model;

class Sleepcycle extends DoctrineModel
{
    protected $entity = 'Application\Entity\Sleepcycle';

    public function getSleepcycleById($sleepcycleId, $crits = array()){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array('sc.id', 'sc.datefrom', 'sc.dateto'))
            ->from($this->entity, 'sc')
            ->leftJoin('Application\Entity\Patient', 'p', 'WITH', 'p.id = sc.patientid')
            ->where($qb->expr()->andX(
                $qb->expr()->eq('sc.id', $sleepcycleId)
            ));

        $query = $qb->getQuery();
        $result = $query->getScalarResult();
        return $result;
    }

    public function getSleepcyclesByPatient($patientId, $crits = array()){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array('sc.id', 'sc.datefrom', 'sc.dateto'))
            ->from($this->entity, 'sc')
            ->leftJoin('Application\Entity\Patient', 'p', 'WITH', 'p.id = sc.patientid')
            ->where($qb->expr()->andX(
                $qb->expr()->eq('p.id', $patientId)
            ));

        $this->filterResults($qb, $crits);

        $query = $qb->getQuery();
        $result = $query->getScalarResult();
        return $result;
    }

    public function getSleepcycles($crits = array()){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array('sc.id', 'sc.datefrom', 'sc.dateto'))
            ->from($this->entity, 'sc');

        $this->filterResults($qb, $crits);

        $query = $qb->getQuery();
        $result = $query->getScalarResult();
        return $result;
    }

    public function getSleepcycleByIdObj($id){
        $obj = $this->entityManager->find($this->entity, $id);
        return $obj;
    }

    public function filterResults($qb, $crits){
        if($crits['isAsleep'] == true){
            $qb->andWhere($qb->expr()->andX(
                $qb->expr()->isNull('sc.dateto')
            ));
        }

        if(isset($crits['dateFrom'])){
            $qb->andWhere($qb->expr()->andX(
                $qb->expr()->gte('sc.datefrom', ':date_from')
            ));
            $qb->setParameter('date_from', $crits['dateFrom'], \Doctrine\DBAL\Types\Type::DATETIME);
        }

        if(isset($crits['dateTo'])) {
            $qb->andWhere($qb->expr()->andX(
                $qb->expr()->lte('sc.dateto', ':date_to')
            ));
            $qb->setParameter('date_to', $crits['dateTo'], \Doctrine\DBAL\Types\Type::DATETIME);
        }
    }
}