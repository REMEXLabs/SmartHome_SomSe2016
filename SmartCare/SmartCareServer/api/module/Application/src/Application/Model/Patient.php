<?php
namespace Application\Model;

class Patient extends DoctrineModel
{
    public function getPatientById($patientId){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array('patient', 'company'))
            ->from('Application\Entity\Patient', 'patient')
            ->leftJoin('Application\Entity\Company', 'company', 'WITH', 'company.id = patient.companyid')
            ->where($qb->expr()->andX(
                $qb->expr()->eq('patient.id', $patientId)
            ));
        $query = $qb->getQuery();
        $result = $this->formatScalarResult($query->getScalarResult());
        return $result;
    }

    public function getPatients(){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array('patient'))
            ->from('Application\Entity\Patient', 'patient')
            ->leftJoin('Application\Entity\Company', 'company', 'WITH', 'company.id = patient.companyid');
        $query = $qb->getQuery();
        $result = $this->clearAliases($query->getScalarResult());
        return $result;
    }

    public function getPatientByIdObj($patientId){
        $obj = $this->entityManager->find('Application\Entity\Patient', $patientId);
        return $obj;
    }
}