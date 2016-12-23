<?php
namespace Application\Model;

use Doctrine\ORM\EntityManager;

class DoctrineModel
{

    protected $entityManager;
    protected $entityClasses = array();
    protected $entityCache = array();
    protected $helper;

    public function __construct(EntityManager $entityManager)
    {
        $this->entityManager = $entityManager;
    }

    /**
     * @param \Doctrine\ORM\EntityManager $entityManager
     */
    public function setEntityManager($entityManager)
    {
        $this->entityManager = $entityManager;
    }

    /**
     * @return \Doctrine\ORM\EntityManager
     */
    public function getEntityManager()
    {
        return $this->entityManager;
    }

    /**
     * @return Array
     */
    public function getEntityCache($name)
    {
        return $this->entityCache[$name];
    }

    /**
     * @param String $name
     * @param array $values
     */
    public function setEntityCache($name, $values)
    {
        $this->entityCache[$name] = $values;
    }

    /**
     * @param String $name
     */
    public function checkEntityCache($name)
    {
        return isset($this->entityCache[$name]);
    }

    /**
     * @return String
     */
    public function getEntityClass($entityName)
    {
        return $this->entityClasses[$entityName];
    }

    /**
     * @param Plugin $helper
     */
    public function setHelper($helper)
    {
        $this->helper = $helper;
    }

    /**
     * @return Plugin Helper
     */
    public function getHelper()
    {
        if($this->helper == null){
            // error
        }
        return $this->helper;
    }

    public static function setLanguage($lngCode){
        DoctrineModel::$lng = $lngCode;
    }

    /**
     * @return Array
     */
    public function formatScalarResult($results){
        $formattedResult = array();
        foreach($results as $count => $result){
            $formattedResult[$count] = array();
            foreach($result as $key => $value){
                $format = explode('_', $key);
                if(!isset($formattedResult[$count][$format[0]])){
                    $formattedResult[$count][$format[0]] = array();
                }
                $formattedResult[$count][$format[0]][$format[1]] = $value;
            }
        }
        return $formattedResult;
    }

    /**
     * @return Array
     */
    public function clearAliases($results){
        $formattedResult = array();
        foreach($results as $count => $result){
            $formattedResult[$count] = array();
            foreach($result as $key => $value){
                $format = explode('_', $key);
                if(count($format) == 2){
                    $formattedResult[$count][$format[1]] = $value;
                } else {
                    $formattedResult[$count][$key] = $value;
                }
            }
        }
        return $formattedResult;
    }

    public function dump($data){
        echo '<pre>';
        echo \Doctrine\Common\Util\Debug::dump($data);
        echo '###################################';
        echo '</pre>';
    }

    public function flipArraySub($ary, $key){
        $flipped = array();
        foreach($ary as $ele){
            $flipped[$ele[$key]] = $ele;
        }
        return $flipped;
    }

}