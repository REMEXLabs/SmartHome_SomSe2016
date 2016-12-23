<?php
namespace Application\Model;

class Notification extends DoctrineModel
{
    protected $entity = 'Application\Entity\Notification';

    public function getNotificationById($notificationId){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array('n.id', 'n.text', 'n.datetime', 'p.id patientId'))
            ->from($this->entity, 'n')
            ->leftJoin('Application\Entity\Patient', 'p', 'WITH', 'p.id = n.patientid')
            ->where($qb->expr()->andX(
                $qb->expr()->eq('sc.id', $notificationId)
            ));
        $query = $qb->getQuery();
        $result = $this->clearAliases($query->getScalarResult());
        return $result;
    }

    public function getNotifications(){
        $qb = $this->entityManager->createQueryBuilder();
        $qb->select(array('n.id', 'n.text', 'n.datetime', 'p.id patientId'))
            ->from($this->entity, 'n')
            ->leftJoin('Application\Entity\Patient', 'p', 'WITH', 'p.id = n.patientid');

        $query = $qb->getQuery();
        $result = $this->clearAliases($query->getScalarResult());
        return $result;
    }

    public function getNotificationByIdObj($id){
        $obj = $this->entityManager->find($this->entity, $id);
        return $obj;
    }
}