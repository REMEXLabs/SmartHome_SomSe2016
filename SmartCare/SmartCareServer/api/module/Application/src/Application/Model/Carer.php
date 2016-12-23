<?php
namespace Application\Model;

class Carer extends DoctrineModel
{
    public function getPatientsByCarer($carerId, $crits = array()){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array(
                'cp.id carerPatientId',
                'cp.datetime',
                'p.id patientId',
                'p.firstname firstname',
                'p.lastname lastname',
                'p.street street',
                'p.zip zip',
                'p.city city',
                'p.image image',
                'p.isAsleep isAsleep',
            ))
            ->from('Application\Entity\CarerPatient', 'cp')
            ->leftJoin('Application\Entity\Patient', 'p', 'WITH', 'p.id = cp.patientid')
            ->leftJoin('Application\Entity\Carer', 'c', 'WITH', 'c.id = cp.carerid')
            ->where($qb->expr()->andX(
                $qb->expr()->eq('cp.carerid', $carerId)
            ));

        $this->filterResults($qb, $crits);

        $query = $qb->getQuery();
        $result = $this->clearAliases($query->getScalarResult());
        return $result;
    }

    public function getCarer(){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array('carer'))
            ->from('Application\Entity\Carer', 'carer');
        $query = $qb->getQuery();
        $result = $this->clearAliases($query->getScalarResult());
        return $result;
    }

    public function getCarerPatientById($cpId){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array(
                'cp.id carerPatientId',
                'cp.datetime',
                'p.id patientId',
                'p.firstname firstname',
                'p.lastname lastname',
                'p.street street',
                'p.zip zip',
                'p.city city',
                'p.image image',
                'p.isAsleep isAsleep',
            ))
            ->from('Application\Entity\CarerPatient', 'cp')
            ->leftJoin('Application\Entity\Patient', 'p', 'WITH', 'p.id = cp.patientid')
            ->leftJoin('Application\Entity\Carer', 'c', 'WITH', 'c.id = cp.carerid')
            ->where($qb->expr()->andX(
                $qb->expr()->eq('cp.id', $cpId)
            ));
        $query = $qb->getQuery();
        $result = $query->getScalarResult();
        return $result;
    }

    public function getCarerPatients($crits = array()){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array(
                'cp.id carerPatientId',
                'cp.datetime',
                'p.id patientId',
                'p.firstname firstname',
                'p.lastname lastname',
                'p.street street',
                'p.zip zip',
                'p.city city',
                'p.image image',
                'p.isAsleep isAsleep',
            ))
            ->from('Application\Entity\CarerPatient', 'cp')
            ->leftJoin('Application\Entity\Patient', 'p', 'WITH', 'p.id = cp.patientid')
            ->leftJoin('Application\Entity\Carer', 'c', 'WITH', 'c.id = cp.carerid');

        $this->filterResults($qb, $crits);

        $query = $qb->getQuery();
        $result = $query->getScalarResult();
        return $result;
    }

    public function filterResults($qb, $crits){
        if(isset($crits['dateFrom'])){
            $qb->andWhere($qb->expr()->andX(
                $qb->expr()->gte('cp.datetime', ':date_from')
            ));
            $qb->setParameter('date_from', $crits['dateFrom'], \Doctrine\DBAL\Types\Type::DATETIME);
        }
        if(isset($crits['dateTo'])){
            $qb->andWhere($qb->expr()->andX(
                $qb->expr()->lte('cp.datetime', ':date_to')
            ));
            $qb->setParameter('date_to', $crits['dateTo'], \Doctrine\DBAL\Types\Type::DATETIME);
        }
    }
}