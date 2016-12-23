<?php
namespace Application\Model;

class Device extends DoctrineModel
{
    public function getDevicesByPatient($patientId){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array('pd.id', 'd.id deviceId', 'd.name', 'ds.name state', 'ds.id stateId', 'pd.value'))
            ->from('Application\Entity\PatientDevices', 'pd')
            ->leftJoin('Application\Entity\Patient', 'p', 'WITH', 'p.id = pd.patientid')
            ->leftJoin('Application\Entity\Device', 'd', 'WITH', 'd.id = pd.deviceid')
            ->leftJoin('Application\Entity\DeviceState', 'ds', 'WITH', 'ds.id = pd.state')
            ->where($qb->expr()->andX(
                $qb->expr()->eq('p.id', $patientId)
            ));
        $query = $qb->getQuery();
        $result = $query->getScalarResult();
        return $result;
    }

    public function getDevices(){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array('device'))
            ->from('Application\Entity\Device', 'device');
        $query = $qb->getQuery();
        $result = $this->clearAliases($query->getScalarResult());
        return $result;
    }

    public function getPatientDeviceById($pdId){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array('pd.id', 'p.id patientId', 'd.name', 'd.id deviceId', 'ds.name state', 'ds.id stateId', 'pd.value'))
            ->from('Application\Entity\PatientDevices', 'pd')
            ->leftJoin('Application\Entity\Patient', 'p', 'WITH', 'p.id = pd.patientid')
            ->leftJoin('Application\Entity\Device', 'd', 'WITH', 'd.id = pd.deviceid')
            ->leftJoin('Application\Entity\DeviceState', 'ds', 'WITH', 'ds.id = pd.state')
            ->where($qb->expr()->andX(
                $qb->expr()->eq('pd.id', $pdId)
            ));
        $query = $qb->getQuery();
        $result = $query->getScalarResult();
        return $result;
    }

    public function getPatientDevices(){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array('pd.id', 'p.id patientId', 'd.name', 'd.id deviceId', 'ds.name state', 'ds.id stateId', 'pd.value'))
            ->from('Application\Entity\PatientDevices', 'pd')
            ->leftJoin('Application\Entity\Patient', 'p', 'WITH', 'p.id = pd.patientid')
            ->leftJoin('Application\Entity\Device', 'd', 'WITH', 'd.id = pd.deviceid')
            ->leftJoin('Application\Entity\DeviceState', 'ds', 'WITH', 'ds.id = pd.state');
        $query = $qb->getQuery();
        $result = $query->getScalarResult();
        return $result;
    }

    public function getPatientDeviceByIdObj($patientDeviceId){
        $obj = $this->entityManager->find('Application\Entity\PatientDevices', $patientDeviceId);
        return $obj;
    }

    public function getDeviceStateByIdObj($deviceStateId){
        $obj = $this->entityManager->find('Application\Entity\DeviceState', $deviceStateId);
        return $obj;
    }
}